package it.unisa.outly.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.util.List;

import it.unisa.outly.dto.ActivitySummary;
import it.unisa.outly.dto.ActivityDetail;
import it.unisa.outly.dto.ActivitySearchRequest;
import it.unisa.outly.dto.ActivitySearchResult;
import it.unisa.outly.dto.ReservationResponse;
import it.unisa.outly.dto.ReservationParticipantsResponse;
import it.unisa.outly.dto.ErrorResponse;
import it.unisa.outly.network.ApiClient;
import it.unisa.outly.network.ApiService;
import it.unisa.outly.network.SessionCookieJar;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class ActivityRepository {
    public interface ResultCallback {
        void onSuccess(List<ActivitySummary> activities);

        void onFailure(String message, boolean sessionExpired);
    }

    public interface SearchResultCallback {
        void onSuccess(List<ActivitySearchResult> results);

        void onFailure(String message, boolean sessionExpired);
    }

    public interface DetailCallback {
        void onSuccess(ActivityDetail activity);

        void onFailure(String message, boolean sessionExpired);
    }

    public interface ReservationCallback {
        void onSuccess(ReservationResponse reservation);

        void onFailure(String message, boolean sessionExpired);
    }

    public interface ParticipantsCallback {
        void onSuccess(ReservationParticipantsResponse participants);

        void onFailure(String message, boolean sessionExpired);
    }

    private final ApiService apiService;
    private final SessionCookieJar cookieJar;
    private final Gson gson = new Gson();

    public ActivityRepository(Context context) {
        apiService = ApiClient.getApiService(context);
        cookieJar = ApiClient.getCookieJar(context);
    }

    public void getActivities(ResultCallback callback) {
        apiService.getActivities().enqueue(new Callback<List<ActivitySummary>>() {
            @Override
            public void onResponse(Call<List<ActivitySummary>> call,
                                   Response<List<ActivitySummary>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else if (response.code() == 401) {
                    cookieJar.clearSessionCookie();
                    callback.onFailure("La sessione è scaduta. Accedi di nuovo.", true);
                    closeErrorBody(response);
                } else {
                    callback.onFailure(errorMessage(response, "Impossibile caricare le attività."),
                            false);
                }
            }

            @Override
            public void onFailure(Call<List<ActivitySummary>> call, Throwable error) {
                callback.onFailure("Errore di rete durante il caricamento delle attività.", false);
            }
        });
    }

    public void getActivity(Long activityId, DetailCallback callback) {
        apiService.getActivity(activityId).enqueue(new Callback<ActivityDetail>() {
            @Override
            public void onResponse(Call<ActivityDetail> call, Response<ActivityDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    boolean sessionExpired = response.code() == 401;
                    if (sessionExpired) {
                        cookieJar.clearSessionCookie();
                    }
                    callback.onFailure(errorMessage(response,
                            response.code() == 404 ? "Attività non trovata."
                                    : response.code() == 403
                                    ? "Non sei autorizzato a visualizzare questa attività."
                                    : sessionExpired ? "La sessione è scaduta. Accedi di nuovo."
                                    : "Impossibile caricare i dettagli dell'attività."),
                            sessionExpired);
                }
            }

            @Override
            public void onFailure(Call<ActivityDetail> call, Throwable error) {
                callback.onFailure("Errore di rete durante il caricamento dell'attività.", false);
            }
        });
    }

    public void searchActivities(ActivitySearchRequest request, SearchResultCallback callback) {
        apiService.searchActivities(request).enqueue(new Callback<List<ActivitySearchResult>>() {
            @Override
            public void onResponse(Call<List<ActivitySearchResult>> call,
                                   Response<List<ActivitySearchResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String fallback = response.code() == 400
                            ? "Uno o più criteri di ricerca non sono validi."
                            : response.code() == 401
                            ? "La sessione è scaduta. Accedi di nuovo."
                            : "Impossibile cercare le attività.";
                    boolean sessionExpired = response.code() == 401;
                    if (sessionExpired) {
                        cookieJar.clearSessionCookie();
                    }
                    callback.onFailure(errorMessage(response, fallback), sessionExpired);
                }
            }

            @Override
            public void onFailure(Call<List<ActivitySearchResult>> call, Throwable error) {
                callback.onFailure("Errore di rete durante la ricerca delle attività.", false);
            }
        });
    }

    public void bookActivity(Long activityId, ReservationCallback callback) {
        apiService.bookActivity(activityId).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call,
                                   Response<ReservationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    boolean sessionExpired = response.code() == 401;
                    if (sessionExpired) {
                        cookieJar.clearSessionCookie();
                    }
                    callback.onFailure(errorMessage(response,
                            response.code() == 409 ? "Prenotazione non possibile: verifica disponibilità e requisiti."
                                    : response.code() == 401 ? "La sessione è scaduta. Accedi di nuovo."
                                    : "Impossibile completare la prenotazione."), sessionExpired);
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable error) {
                callback.onFailure("Errore di rete durante la prenotazione.", false);
            }
        });
    }

    public void cancelReservation(Long reservationId, ReservationCallback callback) {
        apiService.cancelReservation(reservationId).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call,
                                   Response<ReservationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    boolean sessionExpired = response.code() == 401;
                    if (sessionExpired) {
                        cookieJar.clearSessionCookie();
                    }
                    callback.onFailure(errorMessage(response,
                            response.code() == 409 ? "La prenotazione non può essere annullata."
                                    : response.code() == 401 ? "La sessione è scaduta. Accedi di nuovo."
                                    : "Impossibile annullare la prenotazione."), sessionExpired);
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable error) {
                callback.onFailure("Errore di rete durante l'annullamento.", false);
            }
        });
    }

    public void getReservationParticipants(Long activityId, ParticipantsCallback callback) {
        apiService.getReservationParticipants(activityId)
                .enqueue(new Callback<ReservationParticipantsResponse>() {
                    @Override
                    public void onResponse(Call<ReservationParticipantsResponse> call,
                                           Response<ReservationParticipantsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            boolean sessionExpired = response.code() == 401;
                            if (sessionExpired) {
                                cookieJar.clearSessionCookie();
                            }
                            callback.onFailure(errorMessage(response,
                                    response.code() == 403
                                            ? "Non sei autorizzato a consultare questo gruppo."
                                            : response.code() == 401
                                            ? "La sessione è scaduta. Accedi di nuovo."
                                            : "Impossibile recuperare i partecipanti."),
                                    sessionExpired);
                        }
                    }

                    @Override
                    public void onFailure(Call<ReservationParticipantsResponse> call, Throwable error) {
                        callback.onFailure(
                                "Errore di rete durante il recupero dei partecipanti.", false);
                    }
                });
    }

    private String errorMessage(Response<?> response, String fallback) {
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
            try {
                ErrorResponse error = gson.fromJson(errorBody.string(), ErrorResponse.class);
                if (error != null && error.getMessaggio() != null
                        && !error.getMessaggio().trim().isEmpty()) {
                    return error.getMessaggio();
                }
            } catch (IOException | JsonParseException exception) {
                return fallback;
            }
        }
        return fallback;
    }

    private void closeErrorBody(Response<?> response) {
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
            errorBody.close();
        }
    }
}
