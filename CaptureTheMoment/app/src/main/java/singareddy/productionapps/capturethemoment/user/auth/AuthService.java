package singareddy.productionapps.capturethemoment.user.auth;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import singareddy.productionapps.capturethemoment.DataSyncListener;
import singareddy.productionapps.capturethemoment.card.get.SmallCardDownloadListener;
import singareddy.productionapps.capturethemoment.models.Card;
import singareddy.productionapps.capturethemoment.utils.AppUtilities;
import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Book;
import singareddy.productionapps.capturethemoment.models.User;
import singareddy.productionapps.capturethemoment.user.profile.ProfileListener;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.User.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.Firebase.*;

public class AuthService {
    private static String TAG = "AuthService";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDB;
    private FirebaseStorage mFirebaseST;
    private String verificationId;

    private AuthListener.EmailLogin emailLoginListener;
    private AuthListener.Mobile mobileAuthListener;
    private AuthListener.EmailSignup emailSignupListener;
    private DataSyncListener dataSyncListener;
    private ProfileListener profileListener;
    private SmallCardDownloadListener smallCardDownloadListener;
    private File internalStorage;
    private int numberOfOwnedBooks;
    private int numberOfBooksDownloaded;
    private int numberOfSharedBooks;
    private int numberOfCardsInThisBook;
    private HashMap<String, Boolean> cardImagePairsMap;

    public AuthService () {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDB = FirebaseDatabase.getInstance();
        mFirebaseST = FirebaseStorage.getInstance();
    }

    public void registerEmailUser(String email, String password) {
        OnSuccessListener<AuthResult> registerSuccessListener = new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i(TAG, "onSuccess: *");
                emailSignupListener.onEmailUserRegisterSuccess(email);
            }
        };

        OnFailureListener registerFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                emailSignupListener.onEmailUserRegisterFailure(email, e.getMessage());
            }
        };
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(registerSuccessListener)
                .addOnFailureListener(registerFailureListener);
    }

    public void loginUserWithEmail(String email, String password) {
        OnSuccessListener<AuthResult> loginSuccessListener = new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i(TAG, "onSuccess: *");
                emailLoginListener.onEmailUserLoginSuccess();
            }
        };

        OnFailureListener loginFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                emailLoginListener.onEmailUserLoginFailure("");
            }
        };
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(loginSuccessListener)
                .addOnFailureListener(loginFailureListener);
    }

    public void authorizePhoneCredentials(String mobile, String otpCode) {
        // Use manual OTP and create a credential
        if (otpCode != null && !otpCode.isEmpty()) {
            Log.i(TAG, "authorizePhoneCredentials: MANUAL_OTP");
            // Manual OTP entered.
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);
            verifyCredential(credential);
            return;
        }

        Log.i(TAG, "authorizePhoneCredentials: AUTO-OTP");
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.i(TAG, "onVerificationCompleted: *");
                verifyCredential(phoneAuthCredential);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {
                super.onCodeAutoRetrievalTimeOut(verificationId);
                Log.i(TAG, "onCodeAutoRetrievalTimeOut: *");
                mobileAuthListener.onOtpRetrievalFailed();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.i(TAG, "onCodeSent: *");
                AuthService.this.verificationId = verificationId;
                mobileAuthListener.onOtpSent();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.i(TAG, "onVerificationFailed: " + e.getLocalizedMessage());
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile, 1, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, callbacks);
    }

    public void sendPasswordResetEmail(String email) {
        mFirebaseAuth.sendPasswordResetEmail(email.toLowerCase().trim())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: Email sent to: "+email);
                        emailLoginListener.onPasswordResetMailSent(email);
                    }
                });
    }

    private void verifyCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Mobile verified.");
                            mobileAuthListener.onMobileAuthenticationSuccess();
                        }
                        else {
                            mobileAuthListener.onMobileAuthenticationFailure("LOGIN_FAIL");
                        }
                    }
                });
    }

    public void setupInitialData() {
        // This is where the whole data of the current user will be downloaded
        // from Firebase DB.
        CURRENT_USER = mFirebaseAuth.getCurrentUser();
        CURRENT_USER_ID = CURRENT_USER.getUid();
        LOGIN_PROVIDER = CURRENT_USER.getProviders().get(0);
        internalStorage = DataRepository.getInternalStorageRef();
        numberOfOwnedBooks = 0;
        numberOfSharedBooks = 0;
        numberOfBooksDownloaded = 0;
        Log.i(TAG, "setupInitialData: PROVIDER: "+LOGIN_PROVIDER);
        // Download user profile, if available
        setupUserProfile();
        // Download user profile picture and save it in device
        downloadUserProfilePicture();
        // Download all books that belong to this user
        setupBooksOwnedByUser();
        // Download all books that are shared with this user
        setupBooksSharedWithUser();
    }

    public void setupUserProfile() {
        Log.i(TAG, "setupUserProfile: Setup user profile");
        DatabaseReference currentUserNode = mFirebaseDB.getReference()
                .child(ALL_USERS_NODE)
                .child(FirebaseAuth.getInstance().getUid())
                .child(AppUtilities.FBUser.PROFILE);
        currentUserNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: Profile changed");
                User currentUserProfile = null;
                // If any profile data exists here, snapshot wont be null
                if (dataSnapshot.getValue() == null) {
                    currentUserProfile = new User();
                    // Check the login provider
                    if (LOGIN_PROVIDER.equals(EMAIL_PROVIDER)) {
                        currentUserProfile.setEmailId(CURRENT_USER.getEmail());
                    }
                    else if (LOGIN_PROVIDER.equals(PHONE_PROVIDER)) {
                        Long mobile = Long.parseLong(CURRENT_USER.getPhoneNumber().substring(3));
                        currentUserProfile.setMobile(mobile);
                    }
                }
                else {
                    currentUserProfile = dataSnapshot.getValue(User.class);
                    updateUserProfileInFirebaseAuth(currentUserProfile.getName());
                }
                // This user must be saved in cache
                dataSyncListener.onUserProfileDownloaded(currentUserProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference registeredUsersNode = mFirebaseDB.getReference()
                .child(ALL_REGISTERED_USERS_NODE)
                .child(FirebaseAuth.getInstance().getUid());
        OnCompleteListener<Void> completeListener1 = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "onComplete: User added in registered users list.");
                }
                else {
                    Log.i(TAG, "onComplete: User was not added in the registered users list.");
                }
            }
        };
        // Check if the user is phone or email authenticated
        if (mFirebaseAuth.getCurrentUser().getProviders().get(0).equals(EMAIL_PROVIDER)) {
            // Email provider - Add email for this UID
            registeredUsersNode.setValue(
                    FirebaseAuth.getInstance().getCurrentUser().getEmail())
                    .addOnCompleteListener(completeListener1);
        }
        else if (mFirebaseAuth.getCurrentUser().getProviders().get(0).equals(PHONE_PROVIDER)) {
            // Phone provider - Add phone for this UID
//            registeredUsersNode.setValue(Long.parseLong(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3))).addOnCompleteListener(completeListener1);
            registeredUsersNode.setValue(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().substring(3)).addOnCompleteListener(completeListener1);
        }
    }

    private void updateUserProfileInFirebaseAuth(String userDisplayName) {
        UserProfileChangeRequest profileChangeRequest =
                new UserProfileChangeRequest.Builder()
                .setDisplayName(userDisplayName)
                .build();
        mFirebaseAuth.getCurrentUser().updateProfile(profileChangeRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: Profile update success in firebase auth.");
                    }
                });
    }

    public void updateUserProfile(User user) {
        DatabaseReference newUserNode = mFirebaseDB.getReference()
                .child(ALL_USERS_NODE)
                .child(FirebaseAuth.getInstance().getUid())
                .child("profile");

        OnSuccessListener successListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                setupUserProfile();
                profileListener.onProfileUpdated();
            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: User could not be added in DB::: "+e.getLocalizedMessage());
            }
        };

        newUserNode.setValue(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    private void downloadUserProfilePicture() {
        StorageReference profilePicRef = mFirebaseST.getReference()
                .child(CURRENT_USER_ID)
                .child("profile_pic.jpg");
        if (!(profileListener instanceof DataRepository)) {
            // Data repository is not the listener
            // So simply return to the caller.
            Log.i(TAG, "downloadUserProfilePicture: Download Failed.");
            return;
        }
        Uri profilePicUri = ((DataRepository) profileListener).whereToSaveProfilePic();
        profilePicRef.getFile(profilePicUri)
                .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onProgress: Downloading... "+taskSnapshot.getBytesTransferred()+" bytes");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onSuccess: Downloaded!");
                        dataSyncListener.onProfilePictureDownloaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Download Failed! "+e.getLocalizedMessage());
                    }
                });
    }

    public void saveProfilePic(Uri profilePicUri) {
        // Use this Uri and upload the picture to firebase storage
        StorageReference profilePicReference = mFirebaseST.getReference().child(CURRENT_USER_ID).child("profile_pic.jpg");
        profilePicReference.putFile(profilePicUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onProgress: Uploading..."+taskSnapshot.getBytesTransferred()+" bytes");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onSuccess: Uploaded!");
                        profileListener.onProfilePicUpdated();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Upload Failed! "+e.getLocalizedMessage() );
                    }
                });
    }

    public void setupBooksOwnedByUser() {
        DatabaseReference ownedBooks = mFirebaseDB.getReference()
                .child(ALL_USERS_NODE).child(CURRENT_USER_ID)
                .child("profile/ownedBooks");
        ownedBooks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This will be a list of strings
                List<String> ownedBookIds = (ArrayList<String>) dataSnapshot.getValue();
                Log.i(TAG, "onDataChange: Owned books: "+ownedBookIds);
                if (ownedBookIds == null || ownedBookIds.size() == 0) {
                    Log.i(TAG, "onDataChange: No owned books");
                    numberOfOwnedBooks = 0;
                    shouldStopRefreshingUI();
                    return;
                }
                // Store the number of books in an integer
                numberOfOwnedBooks = ownedBookIds.size();
                for (String bookId : ownedBookIds) {
                    downloadBookWithId(bookId, null);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setupBooksSharedWithUser() {
        DatabaseReference sharedBooks = mFirebaseDB.getReference()
                .child(ALL_USERS_NODE).child(CURRENT_USER_ID)
                .child("sharedBooks");
        sharedBooks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This will be a map of bookId:access pairs
                HashMap<String, Boolean> sharedBooks = (HashMap<String, Boolean>) dataSnapshot.getValue();
                Log.i(TAG, "onDataChange: Shared books: "+sharedBooks);
                if (sharedBooks == null || sharedBooks.size() == 0) {
                    Log.i(TAG, "onDataChange: No shared books");
                    numberOfSharedBooks = 0;
                    shouldStopRefreshingUI();
                    return;
                }
                // Store the number of shared books in an integer
                numberOfSharedBooks = sharedBooks.size();
                for (Map.Entry<String, Boolean> entry : sharedBooks.entrySet()) {
                    downloadBookWithId(entry.getKey(), entry.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: Error: "+databaseError.getMessage());
            }
        });

    }

    private void shouldStopRefreshingUI() {
        if (numberOfSharedBooks == 0 && numberOfOwnedBooks == 0) {
            dataSyncListener.shouldStopUILoader();
        }
    }

    /**
     * Fetches a book from firebase given its book id
     * @param bookId
     */
    private void downloadBookWithId(String bookId, Boolean sharedBookAccess) {
        // Fetch the book and add it to shared books list
        mFirebaseDB.getReference()
                .child(AppUtilities.Firebase.ALL_BOOKS_NODE) // books
                .child(bookId) // bookId
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Book fetchedBook = dataSnapshot.getValue(Book.class);
                            // Once a book is downloaded, increase the count
                            numberOfBooksDownloaded += 1;
                            // Check if the number of books downloaded
                            // matches owned books + shared books
                            Log.i(TAG, "onDataChange: Owned books: "+numberOfOwnedBooks);
                            Log.i(TAG, "onDataChange: Shared books: "+numberOfSharedBooks);
                            Log.i(TAG, "onDataChange: Downloaded books: "+numberOfBooksDownloaded);
                            if (numberOfBooksDownloaded == numberOfOwnedBooks+numberOfSharedBooks){
                                // All books are downloaded.
                                // This is the signal to stop the loader in UI
                                dataSyncListener.shouldStopUILoader();
                            }
                            dataSyncListener.onBookDownloadedFromFirebase(fetchedBook, sharedBookAccess);
                            downloadCardsOfBook(bookId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(TAG, "onCancelled: "+databaseError.getMessage());
                    }
                });
    }

    public void downloadCardsOfBook (String bookId) {
        DatabaseReference cardsNode = mFirebaseDB.getReference()
                .child(ALL_BOOKS_NODE)
                .child(bookId)
                .child("cards");
        cardsNode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    dataSyncListener.shouldStopUILoader();
                    return;
                }
                List<String> cardIds = (ArrayList<String>) dataSnapshot.getValue();
                // Store the number of cards in an integer
                numberOfCardsInThisBook = cardIds.size();
                if (numberOfCardsInThisBook == 0) dataSyncListener.shouldStopUILoader();
                cardImagePairsMap = new HashMap<>();
                for (String cardId: cardIds) {
                    downloadCardWithId(cardId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadCardWithId (String cardId) {
        DatabaseReference cardDBRef = mFirebaseDB.getReference()
                .child(ALL_CARDS_NODE)
                .child(cardId);

        cardDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Card card = dataSnapshot.getValue(Card.class);
                dataSyncListener.onCardDownloadedFromFirebase(card, null);
                if (internalStorage == null) internalStorage=DataRepository.getInternalStorageRef();
                for (String path: card.getImagePaths()) {
                    File image = new File(internalStorage, path);
                    Log.i(TAG, "onDataChange: PATH: "+image.getPath());
                    Log.i(TAG, "onDataChange: EXISTS: "+image.exists());
                    try {
                        downloadImageFromFirebaseToPath(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadImageFromFirebaseToPath(String imagePath) throws IOException {
        StorageReference cardStorageRef = mFirebaseST.getReference()
                .child(imagePath);
        Log.i(TAG, "downloadImageFromFirebaseToPath: "+cardStorageRef.getPath());

        File imageFile;
        String [] pathComps = imagePath.split("/");
        imageFile = new File(internalStorage, pathComps[0]);
        if (!imageFile.exists()) imageFile.mkdir();
        imageFile = new File(imageFile, pathComps[1]);
        if (!imageFile.exists()) imageFile.mkdir();
        imageFile = new File(imageFile, pathComps[2]);
        if (imageFile.exists()) {
            Log.i(TAG, "onSuccess: Cards in Book: "+numberOfCardsInThisBook);
            Log.i(TAG, "onSuccess: Cards downloaded: "+cardImagePairsMap.size());
            // If an image exists in the given path,
            // add card-image pair into the map.
            cardImagePairsMap.put(pathComps[1], true);
            if (cardImagePairsMap.size() == numberOfCardsInThisBook) dataSyncListener.shouldStopUILoader();
            return;
        }

        // Create and download the image only if it does not exist on the device
        Log.i(TAG, "downloadImageFromFirebaseToPath: PATH: "+imageFile.getPath());
        boolean newfileCreated = imageFile.createNewFile();

        cardStorageRef.getFile(imageFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onSuccess: Image Downloaded");
                        if (smallCardDownloadListener != null) smallCardDownloadListener.onSmallCardDownloaded();
                        // If an image exists in the given path,
                        // add card-image pair into the map.
                        cardImagePairsMap.put(pathComps[1], true);
                        if (cardImagePairsMap.size() == numberOfCardsInThisBook) dataSyncListener.shouldStopUILoader();
                        Log.i(TAG, "onSuccess: Cards in Book: "+numberOfCardsInThisBook);
                        Log.i(TAG, "onSuccess: Cards downloaded: "+cardImagePairsMap.size());
                    }
                });
    }

    public void setEmailSignupListener(AuthListener.EmailSignup emailSignupListener) {
        this.emailSignupListener = emailSignupListener;
    }

    public void setEmailLoginListener(AuthListener.EmailLogin emailLoginListener) {
        this.emailLoginListener = emailLoginListener;
    }

    public void setMobileAuthListener(AuthListener.Mobile mobileAuthListener) {
        this.mobileAuthListener = mobileAuthListener;
    }

    public void setDataSyncListener(DataSyncListener dataSyncListener) {
        this.dataSyncListener = dataSyncListener;
    }

    public void setProfileListener(ProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    public void setSmallCardDownloadListener(SmallCardDownloadListener smallCardDownloadListener) {
        this.smallCardDownloadListener = smallCardDownloadListener;
    }
}
