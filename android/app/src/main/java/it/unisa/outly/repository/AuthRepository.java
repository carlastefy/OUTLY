package it.unisa.outly.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;

import it.unisa.outly.dto.ErrorResponse;
import it.unisa.outly.dto.LoginRequest;
import it.unisa.outly.dto.LoginResponse;
import it.unisa.outly.dto.ProfileUpdateRequest;
import it.unisa.outly.dto.RegistrationRequest;
import it.unisa.outly.dto.UserProfile;
import it.unisa.outly.network.ApiClient;
import it.unisa.outly.network.ApiService;
import it.unisa.outly.network.SessionCookieJar;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class AuthRepository {
    public interface ResultCallback<T> {
        void onSuccess(T result);

        void onFailure(String message, boolean sessionExpired);
    }

    private final ApiService apiService;
    private final SessionCookieJar cookieJar;
    private final HttpUrl sessionUrl;
    private final Gson gson = new Gson();

    public AuthRepository(Context context) {
        apiService = ApiClient.getApiService(context);
        cookieJar = ApiClient.getCookieJar(context);
        sessionUrl = HttpUrl.get(ApiClient.BASE_URL).newBuilder()
                .addPathSegments("api/utenti/me")
                .build();
    }

    public boolean hasSavedSession() {
        return cookieJar.hasSessionCookie(sessionUrl);
    }

    public void login(String email, String password, ResultCallback<LoginResponse> callback) {
        apiService.login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse result = response.body();
                    if (result == null || result.getIdUtente() == null || result.getRuolo() == null) {
                        callback.onFailure("Risposta di accesso non valida.", false);
                    } else if (!hasSavedSession()) {
                        callback.onFailure("Il server non ha fornito il cookie di sessione.", false);
                    } else {
                        callback.onSuccess(result);
                    }
                    return;
                }
                if (response.code() == 401) {
                    callback.onFailure(errorMessage(response, "Email o password non corretti."), false);
                } else {
                    callback.onFailure(errorMessage(response, "Accesso non riuscito."), false);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable error) {
                callback.onFailure("Errore di rete. Controlla la connessione e riprova.", false);
            }
        });
    }

    public void register(RegistrationRequest request, ResultCallback<Void> callback) {
        apiService.register(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        body.close();
                    }
                    callback.onSuccess(null);
                } else {
                    String fallback;
                    if (response.code() == 409) {
                        fallback = "Esiste già un account con questa e-mail.";
                    } else if (response.code() == 400) {
                        fallback = "I dati inseriti non sono validi.";
                    } else {
                        fallback = "Registrazione non riuscita.";
                    }
                    callback.onFailure(errorMessage(response, fallback), false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable error) {
                callback.onFailure("Errore di rete. Controlla la connessione e riprova.", false);
            }
        });
    }

    public void checkSession(ResultCallback<Boolean> callback) {
        if (!hasSavedSession()) {
            callback.onFailure("Nessuna sessione salvata.", true);
            return;
        }
        apiService.getCurrentUser().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().idUtente != null) {
                        callback.onSuccess(false);
                    } else {
                        callback.onFailure("Risposta profilo non valida.", false);
                    }
                } else if (response.code() == 401) {
                    cookieJar.clearSessionCookie();
                    closeErrorBody(response);
                    callback.onFailure("La sessione è scaduta. Accedi di nuovo.", true);
                } else if (response.code() == 403) {
                    closeErrorBody(response);
                    callback.onSuccess(true);
                } else {
                    callback.onFailure(errorMessage(response, "Impossibile verificare la sessione."), false);
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable error) {
                callback.onFailure("Errore di rete durante la verifica della sessione.", false);
            }
        });
    }

    public void getCurrentProfile(ResultCallback<UserProfile> callback) {
        apiService.getCurrentUser().enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().idUtente != null) {
                    callback.onSuccess(response.body());
                } else if (response.code() == 401) {
                    cookieJar.clearSessionCookie();
                    closeErrorBody(response);
                    callback.onFailure("La sessione è scaduta. Accedi di nuovo.", true);
                } else if (response.isSuccessful()) {
                    callback.onFailure("Risposta profilo non valida.", false);
                } else {
                    callback.onFailure(errorMessage(response, "Impossibile caricare il profilo."), false);
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable error) {
                callback.onFailure("Errore di rete durante il caricamento del profilo.", false);
            }
        });
    }

    public void updateCurrentProfile(ProfileUpdateRequest request,
                                     ResultCallback<UserProfile> callback) {
        apiService.updateCurrentUser(request).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().idUtente != null) {
                    callback.onSuccess(response.body());
                } else if (response.code() == 401) {
                    cookieJar.clearSessionCookie();
                    closeErrorBody(response);
                    callback.onFailure("La sessione è scaduta. Accedi di nuovo.", true);
                } else if (response.isSuccessful()) {
                    callback.onFailure("Risposta profilo non valida.", false);
                } else {
                    String fallback = response.code() == 400
                            ? "I dati inseriti non sono validi."
                            : response.code() == 409
                            ? "Esiste già un account con questa e-mail."
                            : "Impossibile aggiornare il profilo.";
                    callback.onFailure(errorMessage(response, fallback), false);
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable error) {
                callback.onFailure("Errore di rete durante l'aggiornamento del profilo.", false);
            }
        });
    }

    public void deleteCurrentProfile(ResultCallback<Void> callback) {
        apiService.deleteCurrentUser().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    cookieJar.clearSessionCookie();
                    callback.onSuccess(null);
                } else if (response.code() == 401) {
                    cookieJar.clearSessionCookie();
                    closeErrorBody(response);
                    callback.onFailure("La sessione è scaduta. Accedi di nuovo.", true);
                } else {
                    String fallback = response.code() == 409
                            ? "Il profilo non può essere eliminato finché ci sono attività o prenotazioni attive."
                            : "Impossibile eliminare il profilo.";
                    callback.onFailure(errorMessage(response, fallback), false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable error) {
                callback.onFailure("Errore di rete durante l'eliminazione del profilo.", false);
            }
        });
    }

    public void logout(ResultCallback<Void> callback) {
        apiService.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    cookieJar.clearSessionCookie();
                    callback.onSuccess(null);
                } else if (response.code() == 401) {
                    cookieJar.clearSessionCookie();
                    closeErrorBody(response);
                    callback.onFailure("La sessione era già scaduta.", true);
                } else {
                    callback.onFailure(errorMessage(response, "Logout non riuscito."), false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable error) {
                callback.onFailure("Errore di rete durante il logout. Riprova.", false);
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
