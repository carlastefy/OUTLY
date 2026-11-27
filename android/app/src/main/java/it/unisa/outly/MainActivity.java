/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.AlertDialog$Builder
 *  android.content.Context
 *  android.graphics.Color
 *  android.graphics.Typeface
 *  android.graphics.drawable.Drawable
 *  android.graphics.drawable.GradientDrawable
 *  android.os.Bundle
 *  android.view.View
 *  android.view.ViewGroup$LayoutParams
 *  android.widget.EditText
 *  android.widget.FrameLayout
 *  android.widget.FrameLayout$LayoutParams
 *  android.widget.ImageView
 *  android.widget.ImageView$ScaleType
 *  android.widget.LinearLayout
 *  android.widget.LinearLayout$LayoutParams
 *  android.widget.ScrollView
 *  android.widget.TextView
 *  android.widget.Toast
 *  it.unisa.outly.R$drawable
 */
package it.unisa.outly;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.OpenableColumns;
import it.unisa.outly.R;
import it.unisa.outly.dto.ActivitySummary;
import it.unisa.outly.dto.ActivityDetail;
import it.unisa.outly.dto.ActivitySearchRequest;
import it.unisa.outly.dto.ActivitySearchResult;
import it.unisa.outly.dto.ProfileUpdateRequest;
import it.unisa.outly.dto.ReservationResponse;
import it.unisa.outly.dto.ReservationParticipantsResponse;
import it.unisa.outly.dto.UserProfile;
import it.unisa.outly.dto.LoginResponse;
import it.unisa.outly.dto.RegistrationRequest;
import it.unisa.outly.repository.ActivityRepository;
import it.unisa.outly.repository.AuthRepository;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity
extends Activity {
    private static final int INK = Color.rgb((int)38, (int)58, (int)61);
    private static final int TEAL = Color.rgb((int)34, (int)144, (int)126);
    private static final int AQUA = Color.rgb((int)43, (int)166, (int)185);
    private static final int MUTED = Color.rgb((int)91, (int)116, (int)119);
    private static final int PALE = Color.rgb((int)237, (int)247, (int)244);
    private static final int FIELD = Color.rgb((int)246, (int)249, (int)248);
    private static final int BORDER = Color.rgb((int)216, (int)231, (int)227);
    private static final int RED = Color.rgb((int)170, (int)68, (int)68);
    private static final int WHITE = -1;
    private FrameLayout root;
    private LinearLayout mainLayout;
    private FrameLayout contentFrame;
    private LinearLayout bottomNavigation;
    private String currentScreen = "login";
    private final Deque<String> history = new ArrayDeque<String>();
    private final List<ImageView> avatarViews = new ArrayList<>();
    private Uri avatarUri;
    private Uri organizerDocumentUri;
    private String organizerDocumentName;
    private TextView organizerDocumentView;
    private int selectedAdminRequest = -1;
    private int selectedAdminReport = -1;
    private final boolean[] pendingAdminRequests = {true, true};
    private final boolean[] pendingAdminReports = {true, true};
    private String adminName = "Amministratore";
    private String adminEmail = "admin@outly.it";
    private AuthRepository authRepository;
    private ActivityRepository activityRepository;
    private EditText loginEmailInput;
    private EditText loginPasswordInput;
    private TextView loginAction;
    private boolean checkingSavedSession;
    private LoginResponse currentUser;
    private UserProfile currentProfile;
    private TextView profileName;
    private TextView profileSurname;
    private TextView profileEmail;
    private TextView profilePhone;
    private EditText registrationNameInput;
    private EditText registrationSurnameInput;
    private EditText registrationEmailInput;
    private EditText registrationPasswordInput;
    private EditText registrationPhoneInput;
    private EditText homeCityInput;
    private EditText searchBudgetInput;
    private SeekBar socialitySlider;
    private SeekBar technicalDifficultySlider;
    private SeekBar physicalDifficultySlider;
    private TextView accessibilitySelection;
    private TextView activityTypeSelection;
    private ActivitySearchRequest pendingActivitySearch;
    private ActivitySummary selectedActivity;
    private BookingEntry selectedBooking;
    private ReservationParticipantsResponse reservationParticipants;
    private TextView bookingAction;
    private boolean bookingInProgress;
    private final List<BookingEntry> userBookings = new ArrayList<>();

    private static final class BookingEntry {
        private final ActivitySummary activity;
        private final ReservationResponse reservation;

        private BookingEntry(ActivitySummary activity, ReservationResponse reservation) {
            this.activity = activity;
            this.reservation = reservation;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setDecorFitsSystemWindows(true);
        this.getWindow().setStatusBarColor(-1);
        this.getWindow().setNavigationBarColor(-1);
        this.getWindow().getDecorView().setSystemUiVisibility(8208);
        this.root = new FrameLayout((Context)this);
        this.root.setBackgroundColor(-1);
        this.root.setOnApplyWindowInsetsListener((view, insets) -> {
            view.setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
            return insets;
        });
        this.setContentView((View)this.root);
        this.root.requestApplyInsets();
        this.authRepository = new AuthRepository(this);
        this.activityRepository = new ActivityRepository(this);
        this.showLogin();
        this.verifySavedSession();
    }

    private int dp(float value) {
        return (int)(value * this.getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int fill, int stroke, float radius) {
        GradientDrawable drawable2 = new GradientDrawable();
        drawable2.setColor(fill);
        drawable2.setCornerRadius((float)this.dp(radius));
        if (stroke != 0) {
            drawable2.setStroke(this.dp(1.0f), stroke);
        }
        return drawable2;
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout((Context)this);
        layout.setOrientation(1);
        return layout;
    }

    private LinearLayout row() {
        LinearLayout layout = new LinearLayout((Context)this);
        layout.setOrientation(0);
        layout.setGravity(16);
        return layout;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView((Context)this);
        view.setText((CharSequence)value);
        view.setTextSize((float)size);
        view.setTextColor(color);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT, 1);
        }
        view.setGravity(16);
        return view;
    }

    private LinearLayout.LayoutParams lp(int width, int height) {
        return new LinearLayout.LayoutParams(width < 0 ? width : this.dp(width), height < 0 ? height : this.dp(height));
    }

    private void margins(View view, int left, int top, int right, int bottom) {
        ViewGroup.LayoutParams raw = view.getLayoutParams();
        if (raw instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)raw;
            params.setMargins(this.dp(left), this.dp(top), this.dp(right), this.dp(bottom));
            view.setLayoutParams((ViewGroup.LayoutParams)params);
        }
    }

    private ScrollView scroll(LinearLayout content, int horizontalPadding) {
        ScrollView scroll = new ScrollView((Context)this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        content.setPadding(this.dp(horizontalPadding), this.dp(12.0f), this.dp(horizontalPadding), this.dp(24.0f));
        scroll.addView((View)content, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -2));
        return scroll;
    }

    private TextView title(String value) {
        TextView view = this.text(value, 22, INK, true);
        view.setGravity(16);
        view.setPadding(0, 0, 0, this.dp(18.0f));
        return view;
    }

    private TextView action(String value, int fill, int foreground, int stroke, Runnable click) {
        TextView button = this.text(value, 15, foreground, true);
        button.setGravity(17);
        button.setPadding(this.dp(12.0f), 0, this.dp(12.0f), 0);
        button.setMinHeight(this.dp(48.0f));
        button.setBackground((Drawable)this.shape(fill, stroke, 9.0f));
        button.setElevation((float)this.dp(2.0f));
        button.setClickable(true);
        button.setFocusable(true);
        button.setOnClickListener(view -> click.run());
        return button;
    }

    private TextView primaryAction(String value, Runnable click) {
        TextView button = this.action(value, TEAL, -1, TEAL, click);
        button.setMinHeight(this.dp(54.0f));
        return button;
    }

    private TextView softAction(String value, Runnable click) {
        return this.action(value, PALE, TEAL, BORDER, click);
    }

    private TextView dangerAction(String value, Runnable click) {
        return this.action(value, -1, RED, BORDER, click);
    }

    private EditText input(String hint, int inputType, int height) {
        EditText editText = new EditText((Context)this);
        editText.setSingleLine(height < 85 && (inputType & 0x20000) == 0);
        editText.setTextSize(15.0f);
        editText.setTextColor(INK);
        editText.setHintTextColor(Color.rgb((int)151, (int)174, (int)174));
        editText.setHint((CharSequence)hint);
        editText.setInputType(inputType);
        editText.setTypeface(Typeface.DEFAULT);
        editText.setPadding(this.dp(14.0f), this.dp(10.0f), this.dp(14.0f), this.dp(10.0f));
        editText.setBackground((Drawable)this.shape(FIELD, BORDER, 8.0f));
        editText.setMinHeight(this.dp(height));
        return editText;
    }

    private TextView valueBox(String value, int height) {
        TextView view = this.text(value, 15, MUTED, false);
        view.setPadding(this.dp(14.0f), this.dp(8.0f), this.dp(14.0f), this.dp(8.0f));
        view.setBackground((Drawable)this.shape(FIELD, BORDER, 8.0f));
        view.setMinHeight(this.dp(height));
        return view;
    }

    private LinearLayout labeledField(String label, String hint, int inputType, int height) {
        LinearLayout group = this.column();
        TextView heading = this.text(label, 14, INK, true);
        group.addView((View)heading, (ViewGroup.LayoutParams)this.lp(-1, -2));
        EditText editText = this.input(hint, inputType, height);
        LinearLayout.LayoutParams fieldParams = this.lp(-1, height);
        fieldParams.topMargin = this.dp(8.0f);
        group.addView((View)editText, (ViewGroup.LayoutParams)fieldParams);
        return group;
    }

    private LinearLayout labeledValue(String label, String value, int height) {
        LinearLayout group = this.column();
        group.addView((View)this.text(label, 14, INK, true), (ViewGroup.LayoutParams)this.lp(-1, -2));
        TextView box = this.valueBox(value, height);
        LinearLayout.LayoutParams fieldParams = this.lp(-1, height);
        fieldParams.topMargin = this.dp(8.0f);
        group.addView((View)box, (ViewGroup.LayoutParams)fieldParams);
        return group;
    }

    private void showLogin() {
        this.currentScreen = "login";
        this.avatarViews.clear();
        this.root.removeAllViews();
        ScrollView page = new ScrollView((Context)this);
        page.setFillViewport(true);
        LinearLayout form = this.column();
        form.setPadding(this.dp(32.0f), this.dp(14.0f), this.dp(32.0f), this.dp(30.0f));
        TextView heading = this.text("Login", 30, INK, true);
        heading.setGravity(17);
        form.addView((View)heading, (ViewGroup.LayoutParams)this.lp(-1, 54));
        ImageView avatar = this.avatar(48, "Immagine utente");
        LinearLayout.LayoutParams avatarParams = this.lp(48, 48);
        avatarParams.gravity = 1;
        avatarParams.topMargin = this.dp(12.0f);
        avatarParams.bottomMargin = this.dp(28.0f);
        form.addView((View)avatar, (ViewGroup.LayoutParams)avatarParams);
        LinearLayout emailField = this.labeledField("E-MAIL", "Inserisci e-mail", 33, 44);
        this.loginEmailInput = (EditText)emailField.getChildAt(1);
        form.addView((View)emailField, (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout.LayoutParams passParams = this.lp(-1, -2);
        passParams.topMargin = this.dp(12.0f);
        LinearLayout passwordField = this.labeledField("PASSWORD", "Inserisci password", 129, 44);
        this.loginPasswordInput = (EditText)passwordField.getChildAt(1);
        form.addView((View)passwordField, (ViewGroup.LayoutParams)passParams);
        TextView login = this.primaryAction("ACCEDI", this::submitLogin);
        this.loginAction = login;
        LinearLayout.LayoutParams loginParams = this.lp(-1, 54);
        loginParams.topMargin = this.dp(44.0f);
        form.addView((View)login, (ViewGroup.LayoutParams)loginParams);
        TextView register = this.text("sei nuovo? Registrati", 18, AQUA, false);
        register.setOnClickListener(view -> this.showRegistration());
        LinearLayout.LayoutParams registerParams = this.lp(-1, 42);
        registerParams.topMargin = this.dp(2.0f);
        form.addView((View)register, (ViewGroup.LayoutParams)registerParams);
        page.addView((View)form);
        this.root.addView((View)page, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
    }

    private void verifySavedSession() {
        if (!this.authRepository.hasSavedSession()) {
            return;
        }
        this.checkingSavedSession = true;
        if (this.loginAction != null) {
            this.loginAction.setEnabled(false);
        }
        this.authRepository.checkSession(new AuthRepository.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean administrator) {
                checkingSavedSession = false;
                if (!isFinishing()) {
                    openApp(Boolean.TRUE.equals(administrator) ? "admin" : "home", false);
                }
            }

            @Override
            public void onFailure(String message, boolean sessionExpired) {
                checkingSavedSession = false;
                if (isFinishing()) {
                    return;
                }
                if (loginAction != null) {
                    loginAction.setEnabled(true);
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitLogin() {
        if (this.checkingSavedSession) {
            return;
        }
        String email = this.loginEmailInput.getText().toString().trim();
        String password = this.loginPasswordInput.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Inserisci e-mail e password.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.loginAction.setEnabled(false);
        this.loginAction.setText("ACCESSO...");
        this.authRepository.login(email, password, new AuthRepository.ResultCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                currentUser = result;
                currentProfile = null;
                loginPasswordInput.setText("");
                openApp("AMMINISTRATORE".equals(result.getRuolo()) ? "admin" : "home", false);
            }

            @Override
            public void onFailure(String message, boolean sessionExpired) {
                if (isFinishing()) {
                    return;
                }
                loginAction.setEnabled(true);
                loginAction.setText("ACCEDI");
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleActivityFailure(String message, boolean sessionExpired) {
        if (sessionExpired) {
            currentUser = null;
            currentProfile = null;
            userBookings.clear();
            selectedBooking = null;
            reservationParticipants = null;
            history.clear();
            showLogin();
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void logout() {
        this.authRepository.logout(new AuthRepository.ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                currentUser = null;
                currentProfile = null;
                userBookings.clear();
                selectedBooking = null;
                reservationParticipants = null;
                history.clear();
                showLogin();
                Toast.makeText(MainActivity.this, "Logout effettuato.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message, boolean sessionExpired) {
                if (isFinishing()) {
                    return;
                }
                if (sessionExpired) {
                    currentUser = null;
                    currentProfile = null;
                    userBookings.clear();
                    selectedBooking = null;
                    reservationParticipants = null;
                    history.clear();
                    showLogin();
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showRegistration() {
        this.currentScreen = "registration";
        this.avatarViews.clear();
        this.root.removeAllViews();
        ScrollView page = new ScrollView((Context)this);
        page.setFillViewport(true);
        LinearLayout form = this.column();
        form.setPadding(this.dp(32.0f), this.dp(18.0f), this.dp(32.0f), this.dp(28.0f));
        ImageView avatar = this.avatar(48, "Immagine utente");
        LinearLayout.LayoutParams avatarParams = this.lp(48, 48);
        avatarParams.gravity = 1;
        form.addView((View)avatar, (ViewGroup.LayoutParams)avatarParams);
        TextView heading = this.text("Registrazione", 28, INK, true);
        heading.setGravity(17);
        LinearLayout.LayoutParams headingParams = this.lp(-1, 62);
        headingParams.bottomMargin = this.dp(10.0f);
        form.addView((View)heading, (ViewGroup.LayoutParams)headingParams);
        this.registrationNameInput = this.addRegistrationField(
                form, "NOME", "Inserisci nome", 1, 16);
        this.registrationSurnameInput = this.addRegistrationField(
                form, "COGNOME", "Inserisci cognome", 1, 16);
        this.registrationEmailInput = this.addRegistrationField(
                form, "E-MAIL", "Inserisci e-mail", 33, 16);
        this.registrationPasswordInput = this.addRegistrationField(
                form, "PASSWORD", "Inserisci password", 129, 16);
        this.registrationPhoneInput = this.addRegistrationField(
                form, "TELEFONO", "Inserisci telefono", 3, 16);
        TextView confirm = this.primaryAction("CONFERMA", this::submitRegistration);
        this.loginAction = confirm;
        LinearLayout.LayoutParams buttonParams = this.lp(-1, 54);
        buttonParams.topMargin = this.dp(42.0f);
        form.addView((View)confirm, (ViewGroup.LayoutParams)buttonParams);
        page.addView((View)form);
        this.root.addView((View)page, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
    }

    private EditText addRegistrationField(LinearLayout form, String label, String hint,
                                          int inputType, int topMargin) {
        LinearLayout field = this.labeledField(label, hint, inputType, 42);
        LinearLayout.LayoutParams params = this.lp(-1, -2);
        params.topMargin = this.dp(topMargin);
        form.addView((View)field, (ViewGroup.LayoutParams)params);
        return (EditText)field.getChildAt(1);
    }

    private void submitRegistration() {
        String nome = this.registrationNameInput.getText().toString().trim();
        String cognome = this.registrationSurnameInput.getText().toString().trim();
        String email = this.registrationEmailInput.getText().toString().trim();
        String password = this.registrationPasswordInput.getText().toString();
        String telefono = this.registrationPhoneInput.getText().toString().trim();
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty()
                || password.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Compila tutti i campi.", Toast.LENGTH_SHORT).show();
            return;
        }

        this.loginAction.setEnabled(false);
        this.loginAction.setText("REGISTRAZIONE...");
        this.authRepository.register(
                new RegistrationRequest(nome, cognome, email, password, telefono),
                new AuthRepository.ResultCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        if (isFinishing()) {
                            return;
                        }
                        showLogin();
                        loginEmailInput.setText(email);
                        Toast.makeText(MainActivity.this,
                                "Registrazione completata. Accedi con le tue credenziali.",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(String message, boolean sessionExpired) {
                        if (isFinishing()) {
                            return;
                        }
                        loginAction.setEnabled(true);
                        loginAction.setText("CONFERMA");
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private EditText addField(LinearLayout parent, String label, String hint,
                              int type, int height, int top) {
        int classBits = type & 0xF;
        int inputType = classBits == 0 ? 1 | type : type;
        LinearLayout group = this.labeledField(label, hint, inputType, height);
        LinearLayout.LayoutParams params = this.lp(-1, -2);
        params.topMargin = this.dp(top);
        parent.addView((View)group, (ViewGroup.LayoutParams)params);
        return (EditText)group.getChildAt(1);
    }

    private LinearLayout selectField(String label, String[] choices) {
        return this.selectField(label, choices, 44);
    }

    private LinearLayout selectField(String label, String[] choices, int height) {
        LinearLayout group = this.column();
        group.addView((View)this.text(label, 14, INK, true), (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout select = this.row();
        select.setPadding(this.dp(14.0f), 0, this.dp(14.0f), 0);
        select.setBackground((Drawable)this.shape(FIELD, BORDER, 8.0f));
        TextView selectedValue = this.text("Scegli", 15, MUTED, false);
        select.addView((View)selectedValue, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, -1, 1.0f));
        TextView arrow = this.text("\u25be", 18, MUTED, false);
        arrow.setGravity(17);
        select.addView((View)arrow, (ViewGroup.LayoutParams)this.lp(24, -1));
        LinearLayout.LayoutParams selectParams = this.lp(-1, height);
        selectParams.topMargin = this.dp(8.0f);
        group.addView((View)select, (ViewGroup.LayoutParams)selectParams);
        select.setOnClickListener(view -> new AlertDialog.Builder((Context)this).setItems((CharSequence[])choices, (dialog, which) -> selectedValue.setText((CharSequence)choices[which])).show());
        return group;
    }

    private int typeFor(int type) {
        return (type & 0xF) == 0 ? 1 | type : type;
    }

    private ImageView logo(int size) {
        ImageView image = new ImageView((Context)this);
        image.setImageResource(R.drawable.outly_logo);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setContentDescription((CharSequence)"Logo Outly");
        return image;
    }

    private ImageView avatar(int size, String description) {
        ImageView image = new ImageView((Context)this);
        image.setImageResource(R.drawable.user_outline);
        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image.setPadding(this.dp(Math.max(6, size / 6)), this.dp(Math.max(6, size / 6)),
            this.dp(Math.max(6, size / 6)), this.dp(Math.max(6, size / 6)));
        image.setBackground((Drawable)this.shape(PALE, BORDER, size / 2.0f));
        image.setClipToOutline(true);
        image.setElevation((float)this.dp(2.0f));
        image.setContentDescription((CharSequence)description);
        if (this.avatarUri != null) {
            image.setImageURI(this.avatarUri);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setPadding(0, 0, 0, 0);
        }
        return image;
    }

    private ImageView editableAvatar(int size, String description) {
        ImageView image = this.avatar(size, description);
        image.setOnClickListener(view -> this.chooseAvatar());
        this.avatarViews.add(image);
        return image;
    }

    private void chooseAvatar() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Scegli un'immagine profilo"), 1001);
    }

    private void chooseOrganizerDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1002);
    }

    private String displayNameFor(Uri uri) {
        try (Cursor cursor = this.getContentResolver().query(uri,
                new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameColumn = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameColumn >= 0) {
                    return cursor.getString(nameColumn);
                }
            }
        }
        String path = uri.getLastPathSegment();
        return path == null || path.isEmpty() ? "Documento selezionato" : path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.avatarUri = data.getData();
            for (ImageView avatar : this.avatarViews) {
                avatar.setImageURI(this.avatarUri);
                avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
                avatar.setPadding(0, 0, 0, 0);
            }
        } else if (requestCode == 1002 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.organizerDocumentUri = data.getData();
            this.organizerDocumentName = this.displayNameFor(this.organizerDocumentUri);
            if (this.organizerDocumentView != null) {
                this.organizerDocumentView.setText(this.organizerDocumentName);
                this.organizerDocumentView.setTextColor(INK);
            }
        }
    }

    private void openApp(String screen, boolean push) {
        if (push && this.currentScreen != screen) {
            this.history.push(this.currentScreen);
        }
        if (this.mainLayout == null) {
            this.createMainLayout();
        }
        this.root.removeAllViews();
        this.root.addView((View)this.mainLayout, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
        this.showContent(screen);
    }

    private void createMainLayout() {
        this.mainLayout = this.column();
        this.mainLayout.setBackgroundColor(-1);
        LinearLayout header = this.row();
        header.setPadding(this.dp(28.0f), this.dp(10.0f), this.dp(28.0f), this.dp(6.0f));
        TextView menu = this.text("\u2630", 23, INK, false);
        menu.setGravity(17);
        menu.setBackground((Drawable)this.shape(-1, BORDER, 40.0f));
        menu.setOnClickListener(view -> this.showMenu(view));
        header.addView((View)menu, (ViewGroup.LayoutParams)this.lp(42, 42));
        View spacer = new View((Context)this);
        header.addView(spacer, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(1.0f), 1.0f));
        header.addView((View)this.logo(52), (ViewGroup.LayoutParams)this.lp(52, 52));
        this.mainLayout.addView((View)header, (ViewGroup.LayoutParams)this.lp(-1, 64));
        this.contentFrame = new FrameLayout((Context)this);
        this.mainLayout.addView((View)this.contentFrame, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(-1, 0, 1.0f));
        this.bottomNavigation = this.buildBottomNavigation();
        LinearLayout.LayoutParams navParams = this.lp(232, 64);
        navParams.gravity = 1;
        navParams.bottomMargin = this.dp(8.0f);
        this.mainLayout.addView((View)this.bottomNavigation, (ViewGroup.LayoutParams)navParams);
    }

    private LinearLayout buildBottomNavigation() {
        LinearLayout nav = this.row();
        nav.setGravity(17);
        nav.setPadding(this.dp(6.0f), this.dp(4.0f), this.dp(6.0f), this.dp(4.0f));
        nav.setBackground((Drawable)this.shape(-1, -16777216, 22.0f));
        String[] labels = new String[]{"Home", "Prenotazioni", "Profilo"};
        for (int i = 0; i < labels.length; ++i) {
            int index = i;
            LinearLayout tab = this.column();
            tab.setGravity(17);
            View icon;
            if (i == 0) {
                ImageView home = new ImageView((Context)this);
                home.setImageResource(R.drawable.home_outline);
                home.setScaleType(ImageView.ScaleType.FIT_CENTER);
                home.setContentDescription((CharSequence)"Home");
                icon = home;
            } else if (i == 1) {
                ImageView book = new ImageView((Context)this);
                book.setImageResource(R.drawable.open_book);
                book.setScaleType(ImageView.ScaleType.FIT_CENTER);
                book.setContentDescription((CharSequence)"Prenotazioni");
                icon = book;
            } else {
                ImageView user = new ImageView((Context)this);
                user.setImageResource(R.drawable.user_outline);
                user.setScaleType(ImageView.ScaleType.FIT_CENTER);
                user.setContentDescription((CharSequence)"Profilo");
                icon = user;
            }
            TextView label = this.text(labels[i], 11, MUTED, false);
            label.setGravity(17);
            tab.addView(icon, (ViewGroup.LayoutParams)this.lp(-1, 27));
            tab.addView((View)label, (ViewGroup.LayoutParams)this.lp(-1, 19));
            tab.setPadding(this.dp(4.0f), this.dp(2.0f), this.dp(4.0f), this.dp(2.0f));
            tab.setBackground((Drawable)this.shape(-1, 0, 12.0f));
            tab.setOnClickListener(view -> {
                this.history.clear();
                this.showContent(index == 0 ? "home" : (index == 1 ? "bookings" : "profile"));
            });
            LinearLayout.LayoutParams tabParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
            nav.addView((View)tab, (ViewGroup.LayoutParams)tabParams);
        }
        return nav;
    }

    private void updateBottomNavigation() {
        if (this.bottomNavigation == null) {
            return;
        }
        int selected = this.currentScreen.equals("bookings") || this.currentScreen.equals("participants") ? 1 : (this.currentScreen.equals("profile") || this.currentScreen.startsWith("organizer") || this.currentScreen.startsWith("admin") || this.currentScreen.equals("report") ? 2 : 0);
        for (int i = 0; i < this.bottomNavigation.getChildCount(); ++i) {
            LinearLayout tab = (LinearLayout)this.bottomNavigation.getChildAt(i);
            int color = i == selected ? TEAL : MUTED;
            tab.setBackground((Drawable)this.shape(i == selected ? PALE : -1, 0, 12.0f));
            View icon = tab.getChildAt(0);
            ((ImageView)icon).setImageTintList(android.content.res.ColorStateList.valueOf(color));
            ((TextView)tab.getChildAt(1)).setTextColor(color);
            ((TextView)tab.getChildAt(1)).setTypeface(Typeface.DEFAULT, i == selected ? 1 : 0);
        }
    }

    private void showMenu(View anchor) {
        CharSequence[] options = new String[]{"Home", "Risultati ricerca", "Prenotazioni", "Segnalazione cliente", "Area amministratore"};
        new AlertDialog.Builder((Context)this).setItems(options, (dialog, which) -> {
            String[] screens = new String[]{"home", "results", "bookings", "report", "admin"};
            this.navigate(screens[which]);
        }).show();
    }

    private void navigate(String screen) {
        this.openApp(screen, true);
    }

    private void showContent(String screen) {
        this.currentScreen = screen;
        if (this.contentFrame == null) {
            return;
        }
        this.contentFrame.removeAllViews();
        this.contentFrame.addView(switch (screen) {
            case "home" -> this.homePage();
            case "filters" -> this.filtersPage();
            case "results" -> this.resultsPage();
            case "detail" -> this.detailPage();
            case "bookings" -> this.bookingsPage();
            case "participants" -> this.participantsPage();
            case "profile" -> this.profilePage();
            case "profile-edit" -> this.profileEditPage();
            case "organizer-request" -> this.organizerRequestPage();
            case "organizer" -> this.organizerPage();
            case "activity-create" -> this.activityCreatePage();
            case "report" -> this.reportPage();
            case "admin" -> this.adminPage();
            case "admin-edit" -> this.adminEditPage();
            case "admin-requests" -> this.adminRequestsPage();
            case "admin-reports" -> this.adminReportsPage();
            case "admin-request-detail" -> this.adminRequestDetailPage();
            case "admin-report-detail" -> this.adminReportDetailPage();
            default -> this.homePage();
        }, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
        this.bottomNavigation.setVisibility(0);
        this.updateBottomNavigation();
    }

    private View homePage() {
        View home = LayoutInflater.from(this).inflate(R.layout.screen_home, null, false);
        View searchButton = home.findViewById(R.id.homeSearchButton);
        LinearLayout content = (LinearLayout)((ScrollView)home).getChildAt(0);
        LinearLayout locationCard = (LinearLayout)content.getChildAt(1);
        this.homeCityInput = (EditText)locationCard.getChildAt(0);
        searchButton.setOnClickListener(view -> this.navigate("filters"));
        List<LinearLayout> activityCards = new ArrayList<>();
        for (int i = 3; i < content.getChildCount(); i++) {
            View child = content.getChildAt(i);
            if (child instanceof LinearLayout) {
                activityCards.add((LinearLayout)child);
            }
        }
        for (LinearLayout card : activityCards) {
            this.setActivityCard(card, null, "Caricamento attività...");
        }
        this.activityRepository.getActivities(new ActivityRepository.ResultCallback() {
            @Override
            public void onSuccess(List<ActivitySummary> activities) {
                if (!currentScreen.equals("home")) {
                    return;
                }
                for (int i = 0; i < activityCards.size(); i++) {
                    LinearLayout card = activityCards.get(i);
                    if (i < activities.size()) {
                        setActivityCard(card, activities.get(i), null);
                    } else {
                        setActivityCard(card, null,
                                activities.isEmpty() ? "Nessuna attività disponibile."
                                        : "Nessun'altra attività disponibile.");
                    }
                }
            }

            @Override
            public void onFailure(String message, boolean sessionExpired) {
                if (sessionExpired) {
                    handleActivityFailure(message, true);
                    return;
                }
                if (!currentScreen.equals("home")) {
                    return;
                }
                for (LinearLayout card : activityCards) {
                    setActivityCard(card, null, message);
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
        return home;
    }

    private void setActivityCard(LinearLayout card, ActivitySummary activity, String status) {
        LinearLayout details = (LinearLayout)card.getChildAt(1);
        TextView title = (TextView)details.getChildAt(0);
        TextView placeAndDate = (TextView)details.getChildAt(1);
        TextView price = (TextView)details.getChildAt(2);
        if (activity == null) {
            title.setText(status);
            placeAndDate.setText("");
            price.setText("");
            return;
        }
        title.setText(activity.titolo == null ? "" : activity.titolo);
        String city = activity.luogo == null || activity.luogo.citta == null
                ? "" : activity.luogo.citta;
        String dateAndTime = formatActivityDate(activity.data, activity.ora);
        placeAndDate.setText(dateAndTime.isEmpty() ? city : dateAndTime + " - " + city);
        price.setText("Costo: " + formatActivityPrice(activity.costo));
        card.setOnClickListener(view -> {
            selectedActivity = activity;
            navigate("detail");
        });
    }

    private String formatActivityDate(String date, String time) {
        String formattedDate = formatDateValue(date, "yyyy-MM-dd", "EEE d MMM");
        String formattedTime = formatDateValue(time, "HH:mm:ss", "HH:mm");
        if (formattedTime.isEmpty()) {
            formattedTime = formatDateValue(time, "HH:mm", "HH:mm");
        }
        if (formattedDate.isEmpty() || formattedTime.isEmpty()) {
            return "";
        }
        return formattedDate + " " + formattedTime;
    }

    private String formatDateValue(String value, String inputPattern, String outputPattern) {
        if (value == null) {
            return "";
        }
        SimpleDateFormat input = new SimpleDateFormat(inputPattern, Locale.ITALIAN);
        input.setLenient(false);
        ParsePosition position = new ParsePosition(0);
        Date parsed = input.parse(value, position);
        if (parsed == null || position.getIndex() != value.length()) {
            return "";
        }
        return new SimpleDateFormat(outputPattern, Locale.ITALIAN).format(parsed);
    }

    private String formatActivityPrice(BigDecimal price) {
        if (price == null) {
            return "";
        }
        return NumberFormat.getCurrencyInstance(Locale.ITALY).format(price);
    }

    private View filtersPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Cosa vuoi fare oggi?"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout budgetField = this.labeledField("costo", "\u20ac 0,00", 8194, 44);
        this.searchBudgetInput = (EditText)budgetField.getChildAt(1);
        body.addView((View)budgetField, (ViewGroup.LayoutParams)this.lp(-1, -2));
        if (this.homeCityInput != null && !this.homeCityInput.getText().toString().trim().isEmpty()) {
            body.addView((View)this.text("Città: " + this.homeCityInput.getText().toString().trim(),
                    13, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, 28));
        }
        this.addField(body, "tempo", "0 min", 2, 44, 12);
        this.socialitySlider = this.addFilterSlider(body, "socialit\u00e0", 12);
        this.technicalDifficultySlider = this.addFilterSlider(body, "difficolt\u00e0 tecnica", 12);
        this.physicalDifficultySlider = this.addFilterSlider(body, "difficolt\u00e0 fisica", 12);
        this.accessibilitySelection = this.addFilterSelect(body, "caratteristiche di accessibilit\u00e0",
                new String[]{"Sedia a rotelle", "Ascensore", "Difficolt\u00e0 motorie",
                        "Sensibilit\u00e0 sensoriali", "Interprete LIS"}, 12);
        this.activityTypeSelection = this.addFilterSelect(body, "tipo attivit\u00e0",
                new String[]{"Arte", "Giochi", "Sport", "Natura", "Cultura"}, 12);
        TextView search = this.primaryAction("CERCA ATTIVIT\u00c0", this::submitActivitySearch);
        LinearLayout.LayoutParams actionParams = this.lp(-1, 54);
        actionParams.topMargin = this.dp(20.0f);
        body.addView((View)search, (ViewGroup.LayoutParams)actionParams);
        return this.scroll(body, 24);
    }

    private TextView addFilterSelect(LinearLayout parent, String label, String[] choices, int top) {
        LinearLayout group = this.selectField(label, choices);
        LinearLayout.LayoutParams params = this.lp(-1, -2);
        params.topMargin = this.dp(top);
        parent.addView((View)group, (ViewGroup.LayoutParams)params);
        return (TextView)((LinearLayout)group.getChildAt(1)).getChildAt(0);
    }

    private SeekBar addFilterSlider(LinearLayout parent, String label, int top) {
        String[] levels = new String[]{"Nessuna", "Bassa", "Media", "Alta"};
        LinearLayout group = this.column();
        LinearLayout heading = this.row();
        heading.addView((View)this.text(label, 14, INK, true),
            (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView selectedLevel = this.text(levels[2], 13, TEAL, true);
        heading.addView((View)selectedLevel, (ViewGroup.LayoutParams)this.lp(-2, -2));
        group.addView((View)heading, (ViewGroup.LayoutParams)this.lp(-1, -2));

        SeekBar slider = new SeekBar(this);
        slider.setMax(levels.length - 1);
        slider.setProgress(2);
        slider.setProgressTintList(android.content.res.ColorStateList.valueOf(TEAL));
        slider.setThumbTintList(android.content.res.ColorStateList.valueOf(TEAL));
        slider.setContentDescription(label);
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedLevel.setText(levels[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        group.addView((View)slider, (ViewGroup.LayoutParams)this.lp(-1, 32));
        LinearLayout.LayoutParams params = this.lp(-1, -2);
        params.topMargin = this.dp(top);
        parent.addView((View)group, (ViewGroup.LayoutParams)params);
        return slider;
    }

    private void submitActivitySearch() {
        BigDecimal budgetMax = null;
        String budget = this.searchBudgetInput.getText().toString().trim()
                .replace("\u20ac", "").replace(",", ".").trim();
        if (!budget.isEmpty()) {
            try {
                budgetMax = new BigDecimal(budget);
                if (budgetMax.signum() < 0) {
                    throw new NumberFormatException("Negative budget");
                }
            } catch (NumberFormatException exception) {
                Toast.makeText(this, "Inserisci un budget valido.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String city = this.homeCityInput == null
                ? null : this.homeCityInput.getText().toString().trim();
        if (city != null && city.isEmpty()) {
            city = null;
        }
        String accessibility = this.accessibilitySelection.getText().toString();
        Set<String> accessibilityCriteria = "Scegli".equals(accessibility)
                ? Collections.emptySet() : new LinkedHashSet<>(Collections.singletonList(accessibility));
        String activityType = this.activityTypeSelection.getText().toString();
        if ("Scegli".equals(activityType)) {
            activityType = null;
        }
        this.pendingActivitySearch = new ActivitySearchRequest(
                budgetMax, city, null, accessibilityCriteria, activityType,
                searchLevel(this.socialitySlider), searchLevel(this.technicalDifficultySlider),
                searchLevel(this.physicalDifficultySlider));
        this.navigate("results");
    }

    private View resultsPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Risultati ricerca"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        TextView status = this.text("Ricerca in corso...", 14, MUTED, false);
        body.addView((View)status, (ViewGroup.LayoutParams)this.lp(-1, 48));
        ActivitySearchRequest request = this.pendingActivitySearch;
        if (request == null) {
            request = new ActivitySearchRequest(null, null, null, Collections.emptySet(),
                    null, null, null, null);
        }
        this.activityRepository.searchActivities(request, new ActivityRepository.SearchResultCallback() {
            @Override
            public void onSuccess(List<ActivitySearchResult> results) {
                if (!currentScreen.equals("results")) {
                    return;
                }
                body.removeView(status);
                if (results.isEmpty()) {
                    body.addView((View)text("Nessuna attività trovata.", 14, MUTED, false),
                            (ViewGroup.LayoutParams)lp(-1, 48));
                    return;
                }
                for (ActivitySearchResult result : results) {
                    if (result != null && result.attivita != null) {
                        addSearchActivityCard(body, result);
                    }
                }
                if (body.getChildCount() == 1) {
                    body.addView((View)text("Nessuna attività trovata.", 14, MUTED, false),
                            (ViewGroup.LayoutParams)lp(-1, 48));
                }
            }

            @Override
            public void onFailure(String message, boolean sessionExpired) {
                if (sessionExpired) {
                    handleActivityFailure(message, true);
                    return;
                }
                if (!currentScreen.equals("results")) {
                    return;
                }
                status.setText(message);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
        return this.scroll(body, 24);
    }

    private String searchLevel(SeekBar slider) {
        return new String[]{"NESSUNA", "BASSA", "MEDIA", "ALTA"}[slider.getProgress()];
    }

    private void addSearchActivityCard(LinearLayout parent, ActivitySearchResult result) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_activity, parent, false);
        ActivitySummary activity = result.attivita;
        LinearLayout cardDetails = (LinearLayout)((LinearLayout)card).getChildAt(1);
        ((TextView)cardDetails.getChildAt(0)).setText(activity.titolo == null ? "" : activity.titolo);
        String city = activity.luogo == null || activity.luogo.citta == null
                ? "" : activity.luogo.citta;
        String dateAndTime = formatActivityDate(activity.data, activity.ora);
        ((TextView)cardDetails.getChildAt(1)).setText(
                dateAndTime.isEmpty() ? city : dateAndTime + " - " + city);
        ((TextView)cardDetails.getChildAt(2)).setText(
                result.compatibilitaPercentuale == null
                        ? "" : result.compatibilitaPercentuale + "% compatibile");
        if (activity.titolo != null) {
            ((TextView)((LinearLayout)card).getChildAt(0)).setText(activity.titolo);
        }
        card.setOnClickListener(view -> {
            selectedActivity = activity;
            this.navigate("detail");
        });
        LinearLayout.LayoutParams params = this.lp(-1, -2);
        params.topMargin = this.dp(12.0f);
        parent.addView(card, params);
    }

    private void addActivityCard(LinearLayout parent, int index, boolean organizer) {
        String[] names = new String[]{"CERAMICA PER TUTTI", "RECITAZIONE", "TREKKING"};
        String[] places = new String[]{"Vietri sul Mare", "Nocera Inferiore", "Cava de' Tirreni"};
        String[] prices = new String[]{"25 euro", "20 euro", "10 euro"};
        String[] compatibility = new String[]{"Compatibilit\u00e0 90%", "Compatibilit\u00e0 85%", "Compatibilit\u00e0 78%"};
        String[] dates = new String[]{"il 10/11/2026 ore 10:30", "il 10/04/2026 ore 19:00", "il 15/05/2026 ore 08:00"};
        String[] addresses = new String[]{"Vietri sul Mare, via Vittorio II, 84019", "Nocera Inferiore, via Canale, 84014", "Cava de' Tirreni, Monte Castello, 84013"};
        String[] durations = new String[]{"durata: 2 ore", "durata: 5 ore", "durata: 3 ore"};
        String[] max = new String[]{"numero max di partecipanti: 10", "numero max di partecipanti: 20", "numero max di partecipanti: 15"};
        LinearLayout card = this.column();
        card.setPadding(this.dp(10.0f), this.dp(12.0f), this.dp(12.0f), this.dp(12.0f));
        card.setBackground((Drawable)this.shape(-1, BORDER, 13.0f));
        card.setElevation((float)this.dp(3.0f));
        LinearLayout info = this.row();
        info.setGravity(16);
        ImageView image = this.activityPhoto(index);
        info.addView((View)image, (ViewGroup.LayoutParams)this.lp(92, organizer ? 78 : 74));
        LinearLayout details = this.column();
        details.setPadding(this.dp(10.0f), 0, 0, 0);
        TextView name = this.text(names[index], 15, INK, true);
        details.addView((View)name, (ViewGroup.LayoutParams)this.lp(-1, -2));
        if (organizer) {
            details.addView((View)this.text(dates[index], 12, INK, false), (ViewGroup.LayoutParams)this.lp(-1, -2));
            details.addView((View)this.text(addresses[index], 12, INK, false), (ViewGroup.LayoutParams)this.lp(-1, -2));
            details.addView((View)this.text(durations[index] + "    " + max[index], 11, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, -2));
        } else {
            TextView place = this.text(places[index], 13, MUTED, false);
            LinearLayout.LayoutParams placeParams = this.lp(-1, -2);
            placeParams.topMargin = this.dp(4.0f);
            details.addView((View)place, (ViewGroup.LayoutParams)placeParams);
            TextView price = this.text(prices[index], 16, INK, true);
            LinearLayout.LayoutParams priceParams = this.lp(-1, -2);
            priceParams.topMargin = this.dp(3.0f);
            details.addView((View)price, (ViewGroup.LayoutParams)priceParams);
            TextView match = this.text(compatibility[index], 12, TEAL, true);
            match.setGravity(17);
            match.setBackground((Drawable)this.shape(PALE, 0, 8.0f));
            LinearLayout.LayoutParams matchParams = this.lp(-1, 30);
            matchParams.topMargin = this.dp(5.0f);
            details.addView((View)match, (ViewGroup.LayoutParams)matchParams);
        }
        info.addView((View)details, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, -2, 1.0f));
        card.addView((View)info, (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout.LayoutParams cardParams = this.lp(-1, -2);
        cardParams.bottomMargin = this.dp(16.0f);
        parent.addView((View)card, (ViewGroup.LayoutParams)cardParams);
        if (!organizer) {
            card.setOnClickListener(view -> this.navigate("detail"));
        }
        if (organizer) {
            LinearLayout buttons = this.row();
            TextView edit = this.softAction("MODIFICA", () -> this.navigate("activity-create"));
            TextView delete = this.dangerAction("ELIMINA", () -> this.confirm("Eliminare questa attivit\u00e0?", () -> parent.removeView((View)card)));
            buttons.addView((View)edit, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(42.0f), 1.0f));
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(0, this.dp(42.0f), 1.0f);
            deleteParams.leftMargin = this.dp(10.0f);
            buttons.addView((View)delete, (ViewGroup.LayoutParams)deleteParams);
            LinearLayout.LayoutParams buttonsParams = this.lp(-1, 42);
            buttonsParams.topMargin = this.dp(12.0f);
            card.addView((View)buttons, (ViewGroup.LayoutParams)buttonsParams);
        }
    }

    private ImageView activityPhoto(int index) {
        ImageView image = new ImageView((Context)this);
        int[] resources = new int[]{R.drawable.ceramica, R.drawable.teatro, R.drawable.trekking};
        image.setImageResource(resources[index]);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setBackground((Drawable)this.shape(FIELD, BORDER, 4.0f));
        image.setClipToOutline(true);
        return image;
    }

    private View detailPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Dettagli Attivit\u00e0"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        ActivitySummary activity = selectedActivity;
        LinearLayout hero = this.row();
        int photoIndex = activity == null || activity.idAttivita == null
                ? 0 : (int)Math.floorMod(activity.idAttivita, 3L);
        ImageView photo = this.activityPhoto(photoIndex);
        hero.addView((View)photo, (ViewGroup.LayoutParams)this.lp(142, 112));
        LinearLayout summary = this.column();
        summary.setPadding(this.dp(12.0f), this.dp(10.0f), this.dp(10.0f), this.dp(10.0f));
        summary.setBackground((Drawable)this.shape(PALE, BORDER, 12.0f));
        String title = activity == null || activity.titolo == null ? "Attivit\u00e0" : activity.titolo;
        String dateAndTime = activity == null ? "" : formatActivityDate(activity.data, activity.ora);
        String place = "";
        if (activity != null && activity.luogo != null) {
            place = activity.luogo.indirizzo == null ? "" : activity.luogo.indirizzo;
            if (activity.luogo.citta != null && !activity.luogo.citta.isEmpty()) {
                place = place.isEmpty() ? activity.luogo.citta
                        : place + ", " + activity.luogo.citta;
            }
            if (activity.luogo.provincia != null && !activity.luogo.provincia.isEmpty()) {
                place = place.isEmpty() ? activity.luogo.provincia
                        : place + " (" + activity.luogo.provincia + ")";
            }
        }
        TextView detailTitle = this.text(title, 14, INK, true);
        summary.addView((View)detailTitle, (ViewGroup.LayoutParams)this.lp(-1, -2));
        TextView detailDate = this.text(dateAndTime.isEmpty()
                ? "Data non disponibile" : dateAndTime, 12, MUTED, false);
        summary.addView((View)detailDate, (ViewGroup.LayoutParams)this.lp(-1, -2));
        TextView detailPlace = this.text(place.isEmpty()
                ? "Luogo non disponibile" : place, 12, MUTED, false);
        summary.addView((View)detailPlace, (ViewGroup.LayoutParams)this.lp(-1, -2));
        TextView detailDuration = this.text("Durata non disponibile", 12, MUTED, false);
        summary.addView((View)detailDuration, (ViewGroup.LayoutParams)this.lp(-1, -2));
        TextView detailCapacity = this.text("Capienza non disponibile", 12, MUTED, false);
        summary.addView((View)detailCapacity, (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout.LayoutParams summaryParams = new LinearLayout.LayoutParams(0, this.dp(142.0f), 1.0f);
        summaryParams.leftMargin = this.dp(10.0f);
        hero.addView((View)summary, (ViewGroup.LayoutParams)summaryParams);
        body.addView((View)hero, (ViewGroup.LayoutParams)this.lp(-1, 142));
        TextView descTitle = this.text("Descrizione attivit\u00e0", 16, INK, true);
        LinearLayout.LayoutParams descTitleParams = this.lp(-1, 34);
        descTitleParams.topMargin = this.dp(12.0f);
        body.addView((View)descTitle, (ViewGroup.LayoutParams)descTitleParams);
        TextView description = this.valueBox("Descrizione non disponibile", 62);
        body.addView((View)description, (ViewGroup.LayoutParams)this.lp(-1, 68));
        LinearLayout firstRow = this.row();
        TextView detailCost = this.addPair(firstRow, "Costo", activity == null
                ? "Non disponibile" : formatActivityPrice(activity.costo));
        TextView detailType = this.addPair(firstRow, "Tipologia attivit\u00e0", "Non disponibile");
        LinearLayout.LayoutParams pairParams = this.lp(-1, 68);
        pairParams.topMargin = this.dp(12.0f);
        body.addView((View)firstRow, (ViewGroup.LayoutParams)pairParams);
        LinearLayout secondRow = this.row();
        TextView detailSociality = this.addPair(secondRow,
                "Preferenze di socialit\u00e0", "Non disponibile");
        TextView detailBookings = this.addPair(secondRow,
                "Numero di prenotazioni", "Non disponibile");
        LinearLayout.LayoutParams secondParams = this.lp(-1, 68);
        secondParams.topMargin = this.dp(12.0f);
        body.addView((View)secondRow, (ViewGroup.LayoutParams)secondParams);
        LinearLayout thirdRow = this.row();
        TextView detailTechnical = this.addPair(thirdRow,
                "Difficolt\u00e0 tecnica", "Non disponibile");
        TextView detailPhysical = this.addPair(thirdRow,
                "Difficolt\u00e0 fisica", "Non disponibile");
        LinearLayout.LayoutParams thirdParams = this.lp(-1, 68);
        thirdParams.topMargin = this.dp(12.0f);
        body.addView((View)thirdRow, (ViewGroup.LayoutParams)thirdParams);
        LinearLayout accessibility = this.labeledValue("Caratteristiche di accessibilit\u00e0",
                "Non disponibile", 40);
        TextView detailAccessibility = (TextView)accessibility.getChildAt(1);
        body.addView((View)accessibility, (ViewGroup.LayoutParams)this.lp(-1, 66));
        TextView book = this.primaryAction("PRENOTA", () -> { });
        this.bookingAction = book;
        book.setOnClickListener(view ->
                new AlertDialog.Builder((Context)this)
                        .setMessage((CharSequence)"Vuoi prenotare questa attività?")
                        .setNegativeButton((CharSequence)"ANNULLA", null)
                        .setPositiveButton((CharSequence)"PRENOTA",
                                (dialog, which) -> this.submitBooking())
                        .show());
        LinearLayout.LayoutParams bookParams = this.lp(-1, 58);
        bookParams.topMargin = this.dp(16.0f);
        body.addView((View)book, (ViewGroup.LayoutParams)bookParams);
        if (activity != null && activity.idAttivita != null) {
            Long requestedActivityId = activity.idAttivita;
            activityRepository.getActivity(requestedActivityId,
                    new ActivityRepository.DetailCallback() {
                        @Override
                        public void onSuccess(ActivityDetail detail) {
                            if (isFinishing() || !currentScreen.equals("detail")
                                    || selectedActivity == null
                                    || !requestedActivityId.equals(selectedActivity.idAttivita)) {
                                return;
                            }
                            detailTitle.setText(detail.titolo == null ? "" : detail.titolo);
                            detailDate.setText(formatActivityDate(detail.data, detail.ora));
                            String detailLocation = "";
                            if (detail.luogo != null) {
                                detailLocation = detail.luogo.indirizzo == null
                                        ? "" : detail.luogo.indirizzo;
                                if (detail.luogo.citta != null && !detail.luogo.citta.isEmpty()) {
                                    detailLocation = detailLocation.isEmpty()
                                            ? detail.luogo.citta
                                            : detailLocation + ", " + detail.luogo.citta;
                                }
                                if (detail.luogo.provincia != null
                                        && !detail.luogo.provincia.isEmpty()) {
                                    detailLocation = detailLocation.isEmpty()
                                            ? detail.luogo.provincia
                                            : detailLocation + " (" + detail.luogo.provincia + ")";
                                }
                            }
                            detailPlace.setText(detailLocation.isEmpty()
                                    ? "Luogo non disponibile" : detailLocation);
                            detailDuration.setText(detail.durataMinuti == null
                                    ? "Durata non disponibile"
                                    : "Durata: " + detail.durataMinuti + " minuti");
                            detailCapacity.setText(detail.numeroMassimoPartecipanti == null
                                    ? "Capienza non disponibile"
                                    : "Capienza massima: " + detail.numeroMassimoPartecipanti);
                            description.setText(detail.descrizione == null
                                    ? "Descrizione non disponibile" : detail.descrizione);
                            detailCost.setText(detail.costo == null
                                    ? "Non disponibile" : formatActivityPrice(detail.costo));
                            detailType.setText(detail.tipologiaAttivita == null
                                    ? "Non disponibile" : detail.tipologiaAttivita);
                            detailSociality.setText(detail.preferenzeSocialita == null
                                    ? "Non disponibile" : detail.preferenzeSocialita);
                            detailBookings.setText(detail.numeroPrenotazioni == null
                                    ? "Non disponibile" : detail.numeroPrenotazioni.toString());
                            detailTechnical.setText(detail.difficoltaTecnica == null
                                    ? "Non disponibile" : detail.difficoltaTecnica);
                            detailPhysical.setText(detail.difficoltaFisica == null
                                    ? "Non disponibile" : detail.difficoltaFisica);
                            detailAccessibility.setText(detail.caratteristicheAccessibilita == null
                                    || detail.caratteristicheAccessibilita.isEmpty()
                                    ? "Nessuna" : android.text.TextUtils.join(
                                    ", ", detail.caratteristicheAccessibilita));
                            if (Boolean.FALSE.equals(detail.prenotabile)) {
                                book.setText("NON PRENOTABILE");
                                book.setEnabled(false);
                            }
                        }

                        @Override
                        public void onFailure(String message, boolean sessionExpired) {
                            if (isFinishing()) {
                                return;
                            }
                            if (sessionExpired) {
                                handleActivityFailure(message, true);
                            } else if (currentScreen.equals("detail")) {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        return this.scroll(body, 22);
    }

    private void submitBooking() {
        if (bookingInProgress) {
            return;
        }
        if (selectedActivity == null || selectedActivity.idAttivita == null) {
            Toast.makeText(this, "Seleziona un'attività caricata dal backend.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        ActivitySummary activity = selectedActivity;
        bookingInProgress = true;
        bookingAction.setEnabled(false);
        activityRepository.bookActivity(activity.idAttivita,
                new ActivityRepository.ReservationCallback() {
                    @Override
                    public void onSuccess(ReservationResponse reservation) {
                        bookingInProgress = false;
                        if (isFinishing()) {
                            return;
                        }
                        if (reservation.idPrenotazione == null
                                || reservation.idAttivita == null) {
                            bookingAction.setEnabled(true);
                            Toast.makeText(MainActivity.this,
                                    "Risposta di prenotazione non valida.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        userBookings.removeIf(entry ->
                                entry.reservation.idAttivita.equals(reservation.idAttivita));
                        userBookings.add(new BookingEntry(activity, reservation));
                        bookingAction.setEnabled(true);
                        Toast.makeText(MainActivity.this, "Prenotazione confermata.",
                                Toast.LENGTH_SHORT).show();
                        navigate("bookings");
                    }

                    @Override
                    public void onFailure(String message, boolean sessionExpired) {
                        bookingInProgress = false;
                        if (isFinishing()) {
                            return;
                        }
                        if (sessionExpired) {
                            handleActivityFailure(message, true);
                            return;
                        }
                        bookingAction.setEnabled(true);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private TextView addPair(LinearLayout row, String label, String value) {
        LinearLayout pair = this.column();
        pair.addView((View)this.text(label, 13, INK, true), (ViewGroup.LayoutParams)this.lp(-1, 24));
        pair.addView((View)this.valueBox(value, 42), (ViewGroup.LayoutParams)this.lp(-1, 42));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1, 1.0f);
        params.rightMargin = this.dp(10.0f);
        row.addView((View)pair, (ViewGroup.LayoutParams)params);
        return (TextView)pair.getChildAt(1);
    }

    private View bookingsPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Prenotazioni"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        if (userBookings.isEmpty()) {
            body.addView((View)this.text("Nessuna prenotazione caricata in questa sessione.",
                    14, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, 52));
            return this.scroll(body, 22);
        }
        for (BookingEntry booking : new ArrayList<>(userBookings)) {
            this.addBookingCard(body, booking);
        }
        return this.scroll(body, 22);
    }

    private void addBookingCard(LinearLayout parent, BookingEntry booking) {
        ActivitySummary activity = booking.activity;
        ReservationResponse reservation = booking.reservation;
        LinearLayout card = this.column();
        card.setPadding(this.dp(12.0f), this.dp(12.0f), this.dp(12.0f), this.dp(12.0f));
        card.setBackground((Drawable)this.shape(-1, BORDER, 12.0f));
        card.setElevation((float)this.dp(3.0f));
        LinearLayout upper = this.row();
        upper.setGravity(48);
        upper.addView((View)this.activityPhoto(Math.floorMod(
                reservation.idAttivita.intValue(), 3)), (ViewGroup.LayoutParams)this.lp(92, 80));
        LinearLayout details = this.column();
        details.setPadding(this.dp(12.0f), 0, 0, 0);
        String title = activity == null || activity.titolo == null ? "" : activity.titolo;
        String dateTime = activity == null ? "" : formatActivityDate(activity.data, activity.ora);
        String location = activity == null || activity.luogo == null
                ? "" : activity.luogo.citta;
        details.addView((View)this.text(title, 14, INK, true), (ViewGroup.LayoutParams)this.lp(-1, 30));
        details.addView((View)this.text(dateTime, 12, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, -2));
        details.addView((View)this.text(location, 12, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, -2));
        details.addView((View)this.text("Stato: " + reservation.stato, 12, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, -2));
        upper.addView((View)details, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, -2, 1.0f));
        card.addView((View)upper, (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout buttons = this.row();
        TextView remove = this.dangerAction("ELIMINA", () ->
                this.confirm("Vuoi annullare la prenotazione?",
                        () -> cancelBooking(booking)));
        TextView group = this.softAction("GRUPPO", () -> loadBookingParticipants(booking));
        buttons.addView((View)remove, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(42.0f), 1.0f));
        LinearLayout.LayoutParams groupParams = new LinearLayout.LayoutParams(0, this.dp(42.0f), 1.0f);
        groupParams.leftMargin = this.dp(10.0f);
        buttons.addView((View)group, (ViewGroup.LayoutParams)groupParams);
        LinearLayout.LayoutParams buttonsParams = this.lp(-1, 42);
        buttonsParams.topMargin = this.dp(12.0f);
        card.addView((View)buttons, (ViewGroup.LayoutParams)buttonsParams);
        LinearLayout.LayoutParams cardParams = this.lp(-1, -2);
        cardParams.bottomMargin = this.dp(15.0f);
        parent.addView((View)card, (ViewGroup.LayoutParams)cardParams);
    }

    private void cancelBooking(BookingEntry booking) {
        activityRepository.cancelReservation(booking.reservation.idPrenotazione,
                new ActivityRepository.ReservationCallback() {
                    @Override
                    public void onSuccess(ReservationResponse response) {
                        if (isFinishing()) {
                            return;
                        }
                        userBookings.remove(booking);
                        if (selectedBooking == booking) {
                            selectedBooking = null;
                            reservationParticipants = null;
                        }
                        openApp("bookings", false);
                        Toast.makeText(MainActivity.this, "Prenotazione annullata.",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message, boolean sessionExpired) {
                        if (!isFinishing()) {
                            handleActivityFailure(message, sessionExpired);
                        }
                    }
                });
    }

    private void loadBookingParticipants(BookingEntry booking) {
        activityRepository.getReservationParticipants(booking.reservation.idAttivita,
                new ActivityRepository.ParticipantsCallback() {
                    @Override
                    public void onSuccess(ReservationParticipantsResponse participants) {
                        if (isFinishing()) {
                            return;
                        }
                        selectedBooking = booking;
                        reservationParticipants = participants;
                        navigate("participants");
                    }

                    @Override
                    public void onFailure(String message, boolean sessionExpired) {
                        if (!isFinishing()) {
                            handleActivityFailure(message, sessionExpired);
                        }
                    }
                });
    }

    private View participantsPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("LISTA DEI PARTECIPANTI"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout table = this.column();
        table.setBackground((Drawable)this.shape(-1, BORDER, 4.0f));
        table.setElevation((float)this.dp(3.0f));
        LinearLayout header = this.row();
        header.setBackgroundColor(PALE);
        header.addView((View)this.tableCell("NOME", 1.0f, true), (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(54.0f), 1.0f));
        header.addView((View)this.tableCell("TELEFONO", 1.0f, true), (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(54.0f), 1.4f));
        header.addView((View)this.tableCell("GRADO DI\nAFFIDABILIT\u00c0", 1.0f, true), (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(54.0f), 1.2f));
        table.addView((View)header);
        List<ReservationParticipantsResponse.Participant> people =
                reservationParticipants == null || reservationParticipants.partecipanti == null
                        ? Collections.emptyList() : reservationParticipants.partecipanti;
        for (int i = 0; i < people.size(); ++i) {
            ReservationParticipantsResponse.Participant participant = people.get(i);
            LinearLayout person = this.row();
            person.setBackgroundColor(i % 2 == 0 ? -1 : FIELD);
            String name = ((participant.nome == null ? "" : participant.nome) + " "
                    + (participant.cognome == null ? "" : participant.cognome)).trim();
            person.addView((View)this.tableCell(name, 1.0f, false), (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(48.0f), 1.0f));
            person.addView((View)this.tableCell("—", 1.4f, false), (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(48.0f), 1.4f));
            person.addView((View)this.tableCell("—", 1.2f, false), (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, this.dp(48.0f), 1.2f));
            table.addView((View)person);
        }
        if (people.isEmpty()) {
            table.addView((View)this.text("Nessun altro partecipante attivo.",
                    14, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, 48));
        }
        body.addView((View)table, (ViewGroup.LayoutParams)this.lp(-1, -2));
        TextView report = this.dangerAction("SEGNALA CLIENTE", () -> this.navigate("report"));
        LinearLayout.LayoutParams reportParams = this.lp(-1, 48);
        reportParams.topMargin = this.dp(16.0f);
        body.addView((View)report, (ViewGroup.LayoutParams)reportParams);
        return this.scroll(body, 22);
    }

    private TextView tableCell(String value, float weight, boolean heading) {
        TextView cell = this.text(value, heading ? 12 : 13, heading ? TEAL : INK, heading);
        cell.setGravity(17);
        cell.setPadding(this.dp(4.0f), this.dp(4.0f), this.dp(4.0f), this.dp(4.0f));
        return cell;
    }

    private View profilePage() {
        this.avatarViews.clear();
        LinearLayout body = this.column();
        TextView heading = this.text("Profilo", 27, INK, true);
        heading.setGravity(17);
        body.addView((View)heading, (ViewGroup.LayoutParams)this.lp(-1, 42));
        ImageView avatar = this.avatar(48, "Immagine utente");
        LinearLayout.LayoutParams avatarParams = this.lp(48, 48);
        avatarParams.gravity = 1;
        avatarParams.bottomMargin = this.dp(14.0f);
        body.addView((View)avatar, (ViewGroup.LayoutParams)avatarParams);
        LinearLayout phone = this.labeledValue("NUMERO", "Caricamento...", 44);
        this.profilePhone = (TextView)phone.getChildAt(1);
        body.addView((View)phone, (ViewGroup.LayoutParams)this.lp(-1, 70));
        LinearLayout surname = this.labeledValue("COGNOME", "Caricamento...", 42);
        this.profileSurname = (TextView)surname.getChildAt(1);
        LinearLayout.LayoutParams surnameParams = this.lp(-1, 70);
        surnameParams.topMargin = this.dp(8.0f);
        body.addView((View)surname, (ViewGroup.LayoutParams)surnameParams);
        LinearLayout nameRow = this.labeledValue("NOME", "Caricamento...", 42);
        this.profileName = (TextView)nameRow.getChildAt(1);
        LinearLayout.LayoutParams nameParams = this.lp(-1, 70);
        nameParams.topMargin = this.dp(8.0f);
        body.addView((View)nameRow, (ViewGroup.LayoutParams)nameParams);
        LinearLayout emailRow = this.labeledValue("E-MAIL", "Caricamento...", 44);
        this.profileEmail = (TextView)emailRow.getChildAt(1);
        LinearLayout.LayoutParams emailParams = this.lp(-1, 70);
        emailParams.topMargin = this.dp(8.0f);
        body.addView((View)emailRow, (ViewGroup.LayoutParams)emailParams);
        this.addProfileAction(body, "GESTIONE ATTIVIT\u00c0", PALE, TEAL, () -> this.navigate("organizer"));
        this.addProfileAction(body, "MODIFICA PROFILO", PALE, TEAL, () -> this.navigate("profile-edit"));
        this.addProfileAction(body, "ELIMINA PROFILO", -1, RED,
                () -> this.confirm("Eliminare il profilo?", this::deleteProfile));
        this.addProfileAction(body, "LOGOUT", -1, MUTED, this::logout);
        this.addProfileAction(body, "RICHIESTA ORGANIZZATORE", PALE, TEAL, () -> this.navigate("organizer-request"));
        this.loadCurrentProfile();
        return this.scroll(body, 22);
    }

    private void loadCurrentProfile() {
        this.authRepository.getCurrentProfile(new AuthRepository.ResultCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile profile) {
                if (isFinishing() || !currentScreen.equals("profile")) {
                    return;
                }
                currentProfile = profile;
                profileName.setText(profile.nome == null ? "" : profile.nome);
                profileSurname.setText(profile.cognome == null ? "" : profile.cognome);
                profileEmail.setText(profile.email == null ? "" : profile.email);
                profilePhone.setText(profile.telefono == null ? "" : profile.telefono);
            }

            @Override
            public void onFailure(String message, boolean sessionExpired) {
                if (isFinishing()) {
                    return;
                }
                if (sessionExpired) {
                    currentUser = null;
                    currentProfile = null;
                    userBookings.clear();
                    history.clear();
                    showLogin();
                }
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addProfileAction(LinearLayout body, String label, int fill, int color, Runnable run) {
        TextView action = this.action(label, fill, color, BORDER, run);
        LinearLayout.LayoutParams params = this.lp(-1, 50);
        params.topMargin = this.dp(8.0f);
        body.addView((View)action, (ViewGroup.LayoutParams)params);
    }

    private void deleteProfile() {
        authRepository.deleteCurrentProfile(new AuthRepository.ResultCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                currentUser = null;
                currentProfile = null;
                userBookings.clear();
                selectedBooking = null;
                reservationParticipants = null;
                history.clear();
                showLogin();
                Toast.makeText(MainActivity.this, "Profilo eliminato.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message, boolean sessionExpired) {
                if (!isFinishing()) {
                    if (sessionExpired) {
                        handleActivityFailure(message, true);
                    } else {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private View profileEditPage() {
        this.avatarViews.clear();
        LinearLayout body = this.column();
        body.addView((View)this.title("Modifica profilo"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        ImageView avatar = this.editableAvatar(64, "Modifica immagine profilo");
        LinearLayout.LayoutParams avatarParams = this.lp(64, 64);
        avatarParams.gravity = 1;
        avatarParams.bottomMargin = this.dp(12.0f);
        body.addView((View)avatar, (ViewGroup.LayoutParams)avatarParams);
        EditText name = this.addField(body, "Nome", "", 1, 44, 8);
        EditText surname = this.addField(body, "Cognome", "", 1, 44, 12);
        EditText email = this.addField(body, "E-mail", "", 32, 44, 12);
        EditText phone = this.addField(body, "Telefono", "", 3, 44, 12);
        if (currentProfile != null) {
            name.setText(currentProfile.nome == null ? "" : currentProfile.nome);
            surname.setText(currentProfile.cognome == null ? "" : currentProfile.cognome);
            email.setText(currentProfile.email == null ? "" : currentProfile.email);
            phone.setText(currentProfile.telefono == null ? "" : currentProfile.telefono);
        }
        TextView save = this.primaryAction("CONFERMA", () -> { });
        save.setOnClickListener(view -> {
            String updatedName = name.getText().toString().trim();
            String updatedSurname = surname.getText().toString().trim();
            String updatedEmail = email.getText().toString().trim();
            String updatedPhone = phone.getText().toString().trim();
            if (updatedName.isEmpty() || updatedSurname.isEmpty()
                    || updatedEmail.isEmpty() || updatedPhone.isEmpty()) {
                Toast.makeText(MainActivity.this, "Compila tutti i campi.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            save.setEnabled(false);
            authRepository.updateCurrentProfile(
                    new ProfileUpdateRequest(updatedName, updatedSurname, updatedEmail, updatedPhone),
                    new AuthRepository.ResultCallback<UserProfile>() {
                        @Override
                        public void onSuccess(UserProfile profile) {
                            currentProfile = profile;
                            openApp("profile", false);
                            Toast.makeText(MainActivity.this, "Profilo aggiornato.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String message, boolean sessionExpired) {
                            if (isFinishing()) {
                                return;
                            }
                            if (sessionExpired) {
                                handleActivityFailure(message, true);
                            } else {
                                save.setEnabled(true);
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
        LinearLayout.LayoutParams saveParams = this.lp(-1, 54);
        saveParams.topMargin = this.dp(20.0f);
        body.addView((View)save, (ViewGroup.LayoutParams)saveParams);
        return this.scroll(body, 22);
    }

    private View organizerRequestPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Richiesta Riconoscimento Organizzatore"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        this.addField(body, "Nome dell'attivit\u00e0", "Scrivi qui", 1, 46, 8);
        this.addField(body, "Tipologia attivit\u00e0", "Scrivi qui", 1, 46, 12);
        this.addField(body, "Indirizzo", "es.: Via Roma 13, Roma (RM)", 1, 46, 12);
        this.addField(body, "Partita IVA", "Scrivi qui", 1, 46, 12);
        this.addField(body, "Descrizione attivit\u00e0", "Scrivi qui", 131073, 110, 12);
        LinearLayout documentField = this.column();
        LinearLayout.LayoutParams documentParams = this.lp(-1, -2);
        documentParams.topMargin = this.dp(12.0f);
        body.addView((View)documentField, (ViewGroup.LayoutParams)documentParams);
        documentField.addView((View)this.text("Documento esercizio commerciale", 14, INK, true),
            (ViewGroup.LayoutParams)this.lp(-1, -2));
        this.organizerDocumentView = this.valueBox(
            this.organizerDocumentName == null ? "\ud83d\udcce  Aggiungi documento" : this.organizerDocumentName, 46);
        this.organizerDocumentView.setTextColor(this.organizerDocumentName == null ? MUTED : INK);
        this.organizerDocumentView.setOnClickListener(view -> this.chooseOrganizerDocument());
        this.organizerDocumentView.setContentDescription((CharSequence)"Seleziona documento esercizio commerciale");
        LinearLayout.LayoutParams documentViewParams = this.lp(-1, 46);
        documentViewParams.topMargin = this.dp(8.0f);
        documentField.addView((View)this.organizerDocumentView, (ViewGroup.LayoutParams)documentViewParams);
        TextView send = this.primaryAction("INVIA", () -> Toast.makeText((Context)this, (CharSequence)"Richiesta inviata", (int)0).show());
        LinearLayout.LayoutParams sendParams = this.lp(-1, 54);
        sendParams.topMargin = this.dp(16.0f);
        body.addView((View)send, (ViewGroup.LayoutParams)sendParams);
        return this.scroll(body, 22);
    }

    private View organizerPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Gestione attivit\u00e0"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        TextView add = this.primaryAction("INSERISCI", () -> this.navigate("activity-create"));
        body.addView((View)add, (ViewGroup.LayoutParams)this.lp(-1, 50));
        for (int i = 0; i < 3; ++i) {
            this.addActivityCard(body, i, true);
            if (i == 0) {
                View firstActivity = body.getChildAt(body.getChildCount() - 1);
                LinearLayout.LayoutParams activityParams =
                    (LinearLayout.LayoutParams)firstActivity.getLayoutParams();
                activityParams.topMargin = this.dp(20.0f);
                firstActivity.setLayoutParams(activityParams);
            }
        }
        return this.scroll(body, 22);
    }

    private View activityCreatePage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Inserisci attivit\u00e0"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout photoLine = this.row();
        LinearLayout titleGroup = this.column();
        titleGroup.addView((View)this.text("titolo dell'attivit\u00e0", 14, INK, true), (ViewGroup.LayoutParams)this.lp(-1, -2));
        EditText activityTitle = this.input("Scrivi qui", 1, 52);
        LinearLayout.LayoutParams activityTitleParams = this.lp(-1, 52);
        activityTitleParams.topMargin = this.dp(6.0f);
        titleGroup.addView((View)activityTitle, (ViewGroup.LayoutParams)activityTitleParams);
        photoLine.addView((View)titleGroup, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView photo = this.text("\ud83d\udcce\n\ud83d\udcf7", 22, MUTED, false);
        photo.setGravity(17);
        photo.setBackground((Drawable)this.shape(FIELD, BORDER, 8.0f));
        LinearLayout.LayoutParams photoParams = this.lp(72, 76);
        photoParams.leftMargin = this.dp(10.0f);
        photoLine.addView((View)photo, (ViewGroup.LayoutParams)photoParams);
        photoLine.setOnClickListener(view -> Toast.makeText((Context)this, (CharSequence)"Seleziona una foto dell'attivit\u00e0", (int)0).show());
        body.addView((View)photoLine, (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout.LayoutParams pair1 = this.lp(-1, -2);
        pair1.topMargin = this.dp(8.0f);
        body.addView((View)this.fieldPair("costo", "\u20ac 0,00", 8194, "durata", "0 min", 2), (ViewGroup.LayoutParams)pair1);
        LinearLayout.LayoutParams pair2 = this.lp(-1, -2);
        pair2.topMargin = this.dp(8.0f);
        body.addView((View)this.fieldPair("data & ora", "01/01/2026", 4, "ora", "00:00", 4), (ViewGroup.LayoutParams)pair2);
        LinearLayout.LayoutParams pair3 = this.lp(-1, -2);
        pair3.topMargin = this.dp(8.0f);
        body.addView((View)this.fieldPair("numero max partecipanti", "Numero da 0 a 100", 2, "difficolt\u00e0 fisica", "Scegli", 1), (ViewGroup.LayoutParams)pair3);
        LinearLayout.LayoutParams accessParams = this.lp(-1, -2);
        accessParams.topMargin = this.dp(8.0f);
        body.addView((View)this.selectField("caratteristiche di accessibilit\u00e0", new String[]{"Sedia a rotelle", "Ascensore", "Difficolt\u00e0 motorie", "Sensibilit\u00e0 sensoriali", "Interprete LIS"}, 32), (ViewGroup.LayoutParams)accessParams);
        LinearLayout categories = this.row();
        categories.addView((View)this.selectField("preferenze di socialit\u00e0", new String[]{"Nessuna", "Bassa", "Media", "Alta"}, 32), (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, -2, 1.0f));
        LinearLayout.LayoutParams categoryParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        categoryParams.leftMargin = this.dp(8.0f);
        categories.addView((View)this.selectField("tipologia attivit\u00e0", new String[]{"Arte", "Giochi", "Sport", "Natura", "Cultura"}, 32), (ViewGroup.LayoutParams)categoryParams);
        LinearLayout.LayoutParams technicalParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        technicalParams.leftMargin = this.dp(8.0f);
        categories.addView((View)this.selectField("difficolt\u00e0 tecnica", new String[]{"Nessuna", "Bassa", "Media", "Alta"}, 32), (ViewGroup.LayoutParams)technicalParams);
        LinearLayout.LayoutParams categoriesParams = this.lp(-1, -2);
        categoriesParams.topMargin = this.dp(8.0f);
        body.addView((View)categories, (ViewGroup.LayoutParams)categoriesParams);
        this.addCompactField(body, "descrizione attivit\u00e0", "Scrivi qui", 131073, 60, 8);
        this.addCompactField(body, "luogo", "es.: Via Roma 13, Roma (RM)", 1, 32, 8);
        TextView publish = this.primaryAction("CONFERMA", () -> {
            Toast.makeText((Context)this, (CharSequence)"Attivit\u00e0 pubblicata", (int)0).show();
            this.openApp("organizer", false);
        });
        LinearLayout.LayoutParams publishParams = this.lp(-1, 54);
        publishParams.topMargin = this.dp(12.0f);
        body.addView((View)publish, (ViewGroup.LayoutParams)publishParams);
        return this.scroll(body, 22);
    }

    private LinearLayout fieldPair(String label1, String hint1, int type1, String label2, String hint2, int type2) {
        LinearLayout pair = this.row();
        LinearLayout first = this.compactField(label1, hint1, this.typeFor(type1), 32);
        LinearLayout second = this.compactField(label2, hint2, this.typeFor(type2), 32);
        pair.addView((View)first, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, -2, 1.0f));
        LinearLayout.LayoutParams secondParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        secondParams.leftMargin = this.dp(10.0f);
        pair.addView((View)second, (ViewGroup.LayoutParams)secondParams);
        return pair;
    }

    private LinearLayout compactField(String label, String hint, int inputType, int height) {
        LinearLayout group = this.column();
        group.addView((View)this.text(label, 14, INK, true), (ViewGroup.LayoutParams)this.lp(-1, -2));
        EditText editText = this.input(hint, inputType, height);
        LinearLayout.LayoutParams fieldParams = this.lp(-1, height);
        fieldParams.topMargin = this.dp(6.0f);
        group.addView((View)editText, (ViewGroup.LayoutParams)fieldParams);
        return group;
    }

    private void addCompactField(LinearLayout parent, String label, String hint, int type, int height, int top) {
        LinearLayout group = this.compactField(label, hint, this.typeFor(type), height);
        LinearLayout.LayoutParams params = this.lp(-1, -2);
        params.topMargin = this.dp(top);
        parent.addView((View)group, (ViewGroup.LayoutParams)params);
    }

    private View reportPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Segnalazione Cliente"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        this.addField(body, "Titolo attivit\u00e0", "Ceramica per tutti", 1, 44, 8);
        this.addField(body, "Nome e cognome del cliente da segnalare", "Marco Rossi", 1, 44, 12);
        this.addField(body, "Grado di comportamento", "Media", 1, 44, 12);
        this.addField(body, "Descrizione del comportamento", "Descrivi cosa \u00e8 successo", 131073, 132, 12);
        TextView send = this.primaryAction("SEGNALA", () -> new AlertDialog.Builder((Context)this).setMessage((CharSequence)"Inviare la segnalazione?").setNegativeButton((CharSequence)"ANNULLA", null).setPositiveButton((CharSequence)"SEGNALA", (dialog, which) -> {
            Toast.makeText((Context)this, (CharSequence)"Segnalazione inviata", (int)0).show();
            this.openApp("bookings", false);
        }).show());
        LinearLayout.LayoutParams sendParams = this.lp(-1, 54);
        sendParams.topMargin = this.dp(18.0f);
        body.addView((View)send, (ViewGroup.LayoutParams)sendParams);
        return this.scroll(body, 22);
    }

    private View adminPage() {
        this.avatarViews.clear();
        LinearLayout body = this.column();
        TextView heading = this.text("Profilo amministratore", 25, INK, true);
        heading.setGravity(17);
        body.addView((View)heading, (ViewGroup.LayoutParams)this.lp(-1, 42));
        ImageView avatar = this.avatar(48, "Immagine utente");
        LinearLayout.LayoutParams avatarParams = this.lp(48, 48);
        avatarParams.gravity = 1;
        avatarParams.bottomMargin = this.dp(14.0f);
        body.addView((View)avatar, (ViewGroup.LayoutParams)avatarParams);
        body.addView((View)this.labeledValue("NOME", this.adminName, 44),
            (ViewGroup.LayoutParams)this.lp(-1, 70));
        LinearLayout.LayoutParams emailParams = this.lp(-1, 70);
        emailParams.topMargin = this.dp(8.0f);
        body.addView((View)this.labeledValue("E-MAIL", this.adminEmail, 44),
            (ViewGroup.LayoutParams)emailParams);
        TextView edit = this.softAction("MODIFICA PROFILO", () -> this.navigate("admin-edit"));
        LinearLayout.LayoutParams editParams = this.lp(-1, 50);
        editParams.topMargin = this.dp(12.0f);
        body.addView((View)edit, (ViewGroup.LayoutParams)editParams);

        TextView requests = this.action("GESTIONE RICHIESTE ORGANIZZATORE",
            PALE, TEAL, BORDER, () -> this.navigate("admin-requests"));
        LinearLayout.LayoutParams requestsParams = this.lp(-1, 54);
        requestsParams.topMargin = this.dp(22.0f);
        body.addView((View)requests, (ViewGroup.LayoutParams)requestsParams);
        TextView reports = this.action("GESTIONE SEGNALAZIONI",
            PALE, TEAL, BORDER, () -> this.navigate("admin-reports"));
        LinearLayout.LayoutParams reportsParams = this.lp(-1, 54);
        reportsParams.topMargin = this.dp(12.0f);
        body.addView((View)reports, (ViewGroup.LayoutParams)reportsParams);
        TextView logout = this.action("LOGOUT", WHITE, MUTED, BORDER, this::logout);
        LinearLayout.LayoutParams logoutParams = this.lp(-1, 50);
        logoutParams.topMargin = this.dp(12.0f);
        body.addView((View)logout, (ViewGroup.LayoutParams)logoutParams);
        return this.scroll(body, 22);
    }

    private View adminEditPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Modifica profilo amministratore"),
            (ViewGroup.LayoutParams)this.lp(-1, -2));
        EditText name = this.input(this.adminName, 1, 44);
        EditText email = this.input(this.adminEmail, 33, 44);
        name.setText((CharSequence)this.adminName);
        email.setText((CharSequence)this.adminEmail);
        body.addView((View)this.text("NOME", 14, INK, true), (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout.LayoutParams nameParams = this.lp(-1, 44);
        nameParams.topMargin = this.dp(8.0f);
        body.addView((View)name, (ViewGroup.LayoutParams)nameParams);
        LinearLayout.LayoutParams emailLabelParams = this.lp(-1, -2);
        emailLabelParams.topMargin = this.dp(14.0f);
        body.addView((View)this.text("E-MAIL", 14, INK, true), (ViewGroup.LayoutParams)emailLabelParams);
        LinearLayout.LayoutParams emailParams = this.lp(-1, 44);
        emailParams.topMargin = this.dp(8.0f);
        body.addView((View)email, (ViewGroup.LayoutParams)emailParams);
        TextView save = this.primaryAction("CONFERMA", () -> {
            this.adminName = name.getText().toString().trim();
            this.adminEmail = email.getText().toString().trim();
            Toast.makeText((Context)this, (CharSequence)"Profilo aggiornato", Toast.LENGTH_SHORT).show();
            this.openApp("admin", false);
        });
        LinearLayout.LayoutParams saveParams = this.lp(-1, 54);
        saveParams.topMargin = this.dp(20.0f);
        body.addView((View)save, (ViewGroup.LayoutParams)saveParams);
        return this.scroll(body, 22);
    }

    private String[][] adminRequests() {
        return new String[][]{
            {"Caffè Ecstasy", "Sofia Bianchi", "Bar", "Via Ponzio Pilato 13, Salerno",
                "IT12345678901", "Richiesta di abilitazione al ruolo di organizzatore per il locale Caffè Ecstasy."},
            {"Caffè Letterario", "Luisa Romano", "Attività culturali", "Via Roma 45, Salerno",
                "IT09876543210", "Documento dell'esercizio commerciale allegato alla richiesta."}
        };
    }

    private String[][] adminReports() {
        return new String[][]{
            {"Comportamento inappropriato", "Marco Rossi", "Luca Bianchi",
                "Ceramica per tutti", "Media",
                "Il partecipante ha tenuto un comportamento non rispettoso durante l'attività."},
            {"Mancata partecipazione", "Gaia Verdi", "Paolo Neri", "Trekking", "Bassa",
                "Il partecipante non si è presentato all'attività senza avvisare il gruppo."}
        };
    }

    private View adminRequestsPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Richieste organizzatore"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        String[][] requests = this.adminRequests();
        int visible = 0;
        for (int i = 0; i < requests.length; i++) {
            if (this.pendingAdminRequests[i]) {
                this.addAdminListItem(body, requests[i][0], "Richiesta di " + requests[i][1],
                    requests[i][2] + " · " + requests[i][3], true, i);
                visible++;
            }
        }
        if (visible == 0) body.addView((View)this.text("Non ci sono richieste in sospeso.",
            14, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, 48));
        return this.scroll(body, 22);
    }

    private View adminReportsPage() {
        LinearLayout body = this.column();
        body.addView((View)this.title("Gestione segnalazioni"), (ViewGroup.LayoutParams)this.lp(-1, -2));
        String[][] reports = this.adminReports();
        int visible = 0;
        for (int i = 0; i < reports.length; i++) {
            if (this.pendingAdminReports[i]) {
                this.addAdminListItem(body, reports[i][0], "Segnalazione di " + reports[i][1],
                    "Attività: " + reports[i][3] + " · Partecipante: " + reports[i][2], false, i);
                visible++;
            }
        }
        if (visible == 0) body.addView((View)this.text("Non ci sono segnalazioni in sospeso.",
            14, MUTED, false), (ViewGroup.LayoutParams)this.lp(-1, 48));
        return this.scroll(body, 22);
    }

    private void addAdminListItem(LinearLayout parent, String title, String submittedBy,
                                  String summary, boolean organizerRequest, int index) {
        LinearLayout card = this.column();
        card.setPadding(this.dp(14.0f), this.dp(12.0f), this.dp(14.0f), this.dp(12.0f));
        card.setBackground((Drawable)this.shape(WHITE, BORDER, 12.0f));
        card.setElevation((float)this.dp(2.0f));
        card.addView((View)this.text(title, 16, INK, true), (ViewGroup.LayoutParams)this.lp(-1, -2));
        LinearLayout.LayoutParams senderParams = this.lp(-1, -2);
        senderParams.topMargin = this.dp(4.0f);
        card.addView((View)this.text(submittedBy, 13, TEAL, true), (ViewGroup.LayoutParams)senderParams);
        LinearLayout.LayoutParams summaryParams = this.lp(-1, -2);
        summaryParams.topMargin = this.dp(5.0f);
        card.addView((View)this.text(summary, 13, MUTED, false), (ViewGroup.LayoutParams)summaryParams);
        TextView detail = this.softAction("VISUALIZZA DETTAGLIO", () -> {
            if (organizerRequest) {
                this.selectedAdminRequest = index;
                this.navigate("admin-request-detail");
            } else {
                this.selectedAdminReport = index;
                this.navigate("admin-report-detail");
            }
        });
        LinearLayout.LayoutParams detailParams = this.lp(-1, 42);
        detailParams.topMargin = this.dp(10.0f);
        card.addView((View)detail, (ViewGroup.LayoutParams)detailParams);
        LinearLayout.LayoutParams cardParams = this.lp(-1, -2);
        cardParams.bottomMargin = this.dp(12.0f);
        parent.addView((View)card, (ViewGroup.LayoutParams)cardParams);
        card.setOnClickListener(view -> detail.performClick());
    }

    private View adminRequestDetailPage() {
        if (this.selectedAdminRequest < 0 || !this.pendingAdminRequests[this.selectedAdminRequest]) {
            return this.adminRequestsPage();
        }
        String[] request = this.adminRequests()[this.selectedAdminRequest];
        LinearLayout body = this.column();
        body.addView((View)this.title("Dettaglio richiesta organizzatore"),
            (ViewGroup.LayoutParams)this.lp(-1, -2));
        body.addView((View)this.labeledValue("NOME ATTIVITÀ", request[0], 44),
            (ViewGroup.LayoutParams)this.lp(-1, 70));
        this.addAdminDetailField(body, "RICHIEDENTE", request[1]);
        this.addAdminDetailField(body, "TIPOLOGIA ATTIVITÀ", request[2]);
        this.addAdminDetailField(body, "INDIRIZZO", request[3]);
        this.addAdminDetailField(body, "PARTITA IVA", request[4]);
        this.addAdminDetailField(body, "DESCRIZIONE / DOCUMENTI", request[5]);
        this.addAdminDecisionButtons(body, true, this.selectedAdminRequest);
        return this.scroll(body, 22);
    }

    private View adminReportDetailPage() {
        if (this.selectedAdminReport < 0 || !this.pendingAdminReports[this.selectedAdminReport]) {
            return this.adminReportsPage();
        }
        String[] report = this.adminReports()[this.selectedAdminReport];
        LinearLayout body = this.column();
        body.addView((View)this.title("Dettaglio segnalazione"),
            (ViewGroup.LayoutParams)this.lp(-1, -2));
        body.addView((View)this.labeledValue("MOTIVO", report[0], 44),
            (ViewGroup.LayoutParams)this.lp(-1, 70));
        this.addAdminDetailField(body, "SEGNALATO DA", report[1]);
        this.addAdminDetailField(body, "UTENTE SEGNALATO", report[2]);
        this.addAdminDetailField(body, "ATTIVITÀ", report[3]);
        this.addAdminDetailField(body, "GRADO DI COMPORTAMENTO", report[4]);
        this.addAdminDetailField(body, "DESCRIZIONE", report[5]);
        this.addAdminDecisionButtons(body, false, this.selectedAdminReport);
        return this.scroll(body, 22);
    }

    private void addAdminDetailField(LinearLayout body, String label, String value) {
        int fieldHeight = value.length() > 65 ? 96 : 44;
        LinearLayout.LayoutParams params = this.lp(-1, fieldHeight + 26);
        params.topMargin = this.dp(8.0f);
        body.addView((View)this.labeledValue(label, value, fieldHeight), (ViewGroup.LayoutParams)params);
    }

    private void addAdminDecisionButtons(LinearLayout body, boolean organizerRequest, int index) {
        LinearLayout actions = this.row();
        TextView approve = this.softAction("APPROVA", () ->
            this.resolveAdminDecision(organizerRequest, index, true));
        TextView reject = this.dangerAction("RIFIUTA", () -> this.confirm(
            "Vuoi rifiutare questa richiesta?", () -> this.resolveAdminDecision(organizerRequest, index, false)));
        actions.addView((View)approve, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0,
            this.dp(48.0f), 1.0f));
        LinearLayout.LayoutParams rejectParams = new LinearLayout.LayoutParams(0, this.dp(48.0f), 1.0f);
        rejectParams.leftMargin = this.dp(10.0f);
        actions.addView((View)reject, (ViewGroup.LayoutParams)rejectParams);
        LinearLayout.LayoutParams actionsParams = this.lp(-1, 48);
        actionsParams.topMargin = this.dp(20.0f);
        body.addView((View)actions, (ViewGroup.LayoutParams)actionsParams);
    }

    private void resolveAdminDecision(boolean organizerRequest, int index, boolean approved) {
        if (organizerRequest) {
            this.pendingAdminRequests[index] = false;
        } else {
            this.pendingAdminReports[index] = false;
        }
        Toast.makeText((Context)this, (CharSequence)(approved ? "Operazione approvata" : "Richiesta rifiutata"),
            Toast.LENGTH_SHORT).show();
        this.openApp(organizerRequest ? "admin-requests" : "admin-reports", false);
    }

    private void confirm(String message, Runnable confirmed) {
        new AlertDialog.Builder((Context)this).setMessage((CharSequence)message).setNegativeButton((CharSequence)"ANNULLA", null).setPositiveButton((CharSequence)"CONFERMA", (dialog, which) -> confirmed.run()).show();
    }

    public void onBackPressed() {
        if (!this.history.isEmpty()) {
            this.showContent(this.history.pop());
        } else if (this.currentScreen.equals("registration")) {
            this.showLogin();
        } else if (!this.currentScreen.equals("home") && !this.currentScreen.equals("login")) {
            this.showContent("home");
        } else if (this.currentScreen.equals("login")) {
            super.onBackPressed();
        } else {
            this.showLogin();
        }
    }
}
