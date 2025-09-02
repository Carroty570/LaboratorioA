package com.service;

import com.model.Feedback;
import com.utils.Helpers;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import static com.service.CsvService.*;

/**
 * Gestione feedback su CSV:
 *  - File: data/feedback.csv
 *  - Schema: ID-Feedback,mail,ID-Restaurant,Stars,Comment
 *
 * Regole:
 *  - Un utente (mail) può avere al massimo 1 recensione per ristorante (MIC-xxxxxx).
 *  - upsertReview: se esiste la aggiorna, altrimenti la crea con un nuovo ID (FDB-xxxxxx).
 */
public final class FeedbackService {

    private FeedbackService() {}

    private static final Path FEED_PATH = Paths.get("data", "feedback.csv");
    private static final List<String> FEED_HDR = List.of("ID-Feedback","mail","ID-Restaurant","Stars","Comment");
    private static final String FEED_ID_PREFIX = "FDB-";

    //Create o update

    // Crea o aggiorna la recensione dell'utente per quel ristorante. Ritorna l'ID-Feedback. 
    public static String upsertReview(String restaurantMicId, String userEmail, int stars, String comment) throws IOException {

        if (restaurantMicId == null || restaurantMicId.isBlank()) throw new IllegalArgumentException("ID-Restaurant mancante");
        if (userEmail == null || userEmail.isBlank()) throw new IllegalArgumentException("Mail utente mancante");
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Stelle devono essere tra 1 e 5");

        CsvTable t = CsvService.read(FEED_PATH, true);

        int iId   = col(t, "ID-Feedback", 0);
        int iMail = col(t, "mail", 1);
        int iRest = col(t, "ID-Restaurant", 2);
        int iStar = col(t, "Stars", 3);
        int iComm = col(t, "Comment", 4);

        String foundId = null;

        for (List<String> r : t.getRows()) {

            if (r.size() < 5) continue;
            if (Helpers.eq(r.get(iMail), userEmail) && Helpers.eq(r.get(iRest), restaurantMicId)) {
                // update
                r.set(iStar, String.valueOf(stars));
                r.set(iComm, comment == null ? "" : comment.trim());
                foundId = Helpers.safe(r.get(iId));
                CsvService.rewrite(FEED_PATH, t.getHeader(), t.getRows());
                return foundId;
            }
        }

        // insert
        String newId = nextFeedbackId(t, iId);

        List<String> row = List.of(
                newId,
                userEmail.trim(),
                restaurantMicId.trim(),
                String.valueOf(stars),
                comment == null ? "" : comment.trim()
        );

        CsvService.appendRow(FEED_PATH, row, FEED_HDR);
        return newId;
    }

    //Delete feedback dell'utente corrente 

    public static boolean deleteMyReview(String restaurantMicId, String userEmail) throws IOException {
        CsvTable t = CsvService.read(FEED_PATH, true);
        int iMail = col(t, "mail", 1);
        int iRest = col(t, "ID-Restaurant", 2);

        boolean removed = false;
        List<List<String>> kept = new ArrayList<>();

        for (List<String> r : t.getRows()) {
            if (r.size() < 5) continue;
            if (Helpers.eq(r.get(iMail), userEmail) && Helpers.eq(r.get(iRest), restaurantMicId)) {
                removed = true;
            } else {
                kept.add(r);
            }
        }

        if (removed) CsvService.rewrite(FEED_PATH, t.getHeader(), kept);
        return removed;
    }

    //Read
    // Tutte le recensioni di un ristorante. 

    public static List<Feedback> getReviewsByRestaurant(String restaurantMicId) throws IOException {

        CsvTable t = CsvService.read(FEED_PATH, true);

        int iId   = col(t, "ID-Feedback", 0);
        int iMail = col(t, "mail", 1);
        int iRest = col(t, "ID-Restaurant", 2);
        int iStar = col(t, "Stars", 3);
        int iComm = col(t, "Comment", 4);

        List<Feedback> out = new ArrayList<>();

        for (List<String> r : t.getRows()) {

            if (r.size() < 5) continue;
            if (!Helpers.eq(r.get(iRest), restaurantMicId)) continue;

            Integer stars = Helpers.parseIntOrNull(r.get(iStar));
            if (stars == null) continue;

            out.add(new Feedback(
                    Helpers.safe(r.get(iId)),
                    Helpers.safe(r.get(iMail)),
                    Helpers.safe(r.get(iRest)),
                    stars,
                    Helpers.safe(r.get(iComm))
            ));
        }
        return out;
    }

    // La mia recensione per quel ristorante (se esiste). 

    public static Optional<Feedback> getMyReview(String restaurantMicId, String userEmail) throws IOException {

        for (Feedback f : getReviewsByRestaurant(restaurantMicId)) {
            if (Helpers.eq(f.getMail(), userEmail)) return Optional.of(f);
        }
        return Optional.empty();
    }

    // Media stelle del ristorante (0.0 se nessuna recensione).
    public static double averageStars(String restaurantMicId) throws IOException {

        List<Feedback> list = getReviewsByRestaurant(restaurantMicId);
        if (list.isEmpty()) return 0.0;
        int sum = 0;
        for (Feedback f : list) sum += f.getStars();
        return sum / (double) list.size();
    }

    //Helpers vari
    private static String nextFeedbackId(CsvTable t, int idxId) {
        int max = 0;
        for (List<String> r : t.getRows()) {
            if (r.size() <= idxId) continue;
            String id = Helpers.safe(r.get(idxId));
            if (id.startsWith(FEED_ID_PREFIX)) {
                try {
                    int n = Integer.parseInt(id.substring(FEED_ID_PREFIX.length()));
                    if (n > max) max = n;
                } catch (Exception ignored) {}
            }
        }
        return FEED_ID_PREFIX + String.format("%06d", max + 1);
    }

    private static int col(CsvTable t, String name, int fallback) {
        int idx = t.columnIndex(name);
        return (idx >= 0) ? idx : fallback;
    }


    /* =========================================================
       Risposte del ristoratore
       Usa il file: data/feedback_replies.csv
       Schema: ID-Feedback,owner_mail,Reply
       ========================================================= */

    private static final Path REPLY_PATH  = Paths.get("data", "feedback_replies.csv");
    private static final List<String> REPLY_HDR = List.of("ID-Feedback","owner_mail","Reply");

    public static void replyToFeedback(String feedbackId, String ownerEmail, String reply) throws IOException {

        CsvTable t = CsvService.read(REPLY_PATH, true);
        int iF = col(t, "ID-Feedback", 0);
        int iO = col(t, "owner_mail", 1);
        int iR = col(t, "Reply", 2);

        boolean updated = false;
        for (List<String> r : t.getRows()) {

            if (r.size() < 3) continue;
            if (Helpers.eq(r.get(iF), feedbackId)) {

                r.set(iO, ownerEmail == null ? "" : ownerEmail.trim());
                r.set(iR, reply == null ? "" : reply.trim());
                updated = true; break;
            }
        }
        if (updated) {

            CsvService.rewrite(REPLY_PATH, t.getHeader(), t.getRows());
        } else {

            List<String> row = List.of(feedbackId, ownerEmail == null ? "" : ownerEmail.trim(), reply == null ? "" : reply.trim());
            CsvService.appendRow(REPLY_PATH, row, REPLY_HDR);
        }
    }

    public static Map<String,String> repliesByRestaurant(String restaurantMicId) throws IOException {

        CsvTable feeds = CsvService.read(FEED_PATH, true);
        CsvTable reps  = CsvService.read(REPLY_PATH, true);

        int iIdF  = col(feeds, "ID-Feedback", 0);
        int iRest = col(feeds, "ID-Restaurant", 2);

        Set<String> ids = new HashSet<>();

        for (List<String> r : feeds.getRows()) {

            if (r.size() < 5) continue;
            if (Helpers.eq(r.get(iRest), restaurantMicId)) ids.add(Helpers.safe(r.get(iIdF)));
        }

        int iF = col(reps, "ID-Feedback", 0);
        int iR = col(reps, "Reply", 2);

        Map<String,String> out = new HashMap<>();

        for (List<String> r : reps.getRows()) {

            if (r.size() < 3) continue;

            String fid = Helpers.safe(r.get(iF));
            
            if (ids.contains(fid)) out.put(fid, Helpers.safe(r.get(iR)));
        }
        return out;
    }

}
