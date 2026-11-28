package it.unisa.outly.network;

import it.unisa.outly.dto.LoginRequest;
import it.unisa.outly.dto.LoginResponse;
import it.unisa.outly.dto.RegistrationRequest;
import it.unisa.outly.dto.ActivitySummary;
import it.unisa.outly.dto.ActivityDetail;
import it.unisa.outly.dto.ActivitySearchRequest;
import it.unisa.outly.dto.ActivitySearchResult;
import it.unisa.outly.dto.ReservationResponse;
import it.unisa.outly.dto.ReservationParticipantsResponse;
import it.unisa.outly.dto.UserProfile;
import it.unisa.outly.dto.ProfileUpdateRequest;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PATCH;

public interface ApiService {
    @POST("api/sessioni")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/utenti")
    Call<ResponseBody> register(@Body RegistrationRequest request);

    @GET("api/attivita")
    Call<List<ActivitySummary>> getActivities();

    @GET("api/attivita/{id}")
    Call<ActivityDetail> getActivity(@Path("id") Long activityId);

    @POST("api/attivita/ricerca")
    Call<List<ActivitySearchResult>> searchActivities(@Body ActivitySearchRequest request);

    @POST("api/attivita/{id}/prenotazioni")
    Call<ReservationResponse> bookActivity(@Path("id") Long activityId);

    @DELETE("api/prenotazioni/{id}")
    Call<ReservationResponse> cancelReservation(@Path("id") Long reservationId);

    @GET("api/attivita/{id}/prenotazioni")
    Call<ReservationParticipantsResponse> getReservationParticipants(@Path("id") Long activityId);

    @GET("api/utenti/me")
    Call<UserProfile> getCurrentUser();

    @PATCH("api/utenti/me")
    Call<UserProfile> updateCurrentUser(@Body ProfileUpdateRequest request);

    @DELETE("api/utenti/me")
    Call<Void> deleteCurrentUser();

    @DELETE("api/sessioni/corrente")
    Call<Void> logout();
}
