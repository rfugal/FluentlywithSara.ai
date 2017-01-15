package ai.sara.fluentlywithsaraai.data;

import android.provider.BaseColumns;

/**
 * Created by russ.fugal on 1/12/2017.
 */

public final class UserListContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private UserListContract() {}

    /* Inner class that defines the users */
    public static class UserList implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_BIRTHDATE = "birthdate";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_AUTHENTICATION = "authentication";
        public static final String COLUMN_SYNC = "sync";

        public static final int GENDER_MALE = 0;
        public static final int GENDER_FEMALE = 1;
        public static final int GENDER_OTHER = 2;

        public static final int SYNC_FALSE = 0;
        public static final int SYNC_TRUE = 1;
    }
    /* Inner class that defines the sight word models */
    public static class UserFluency implements BaseColumns {
        public static final String TABLE_NAME = "fluency_model";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_WORD_WEIGHTS = "word_weights";
        public static final String COLUMN_WORDS_FLUENT = "words_fluent";
        public static final String COLUMN_ENCOUNTER_COUNTS = "encounter_counts";
        public static final String COLUMN_USER_SCORE = "user_score";
        public static final String COLUMN_FLUENT_COUNT = "fluent_count";
        public static final String COLUMN_READ_COUNT = "read_count";
        public static final String COLUMN_VERSION = "word_list_version";
    }
    /* Inner class that defines the encounter log */
    public static class WordEncounters implements BaseColumns {
        public static final String TABLE_NAME = "word_encounters";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_TIMESTAMP = "encounter_timestamp";
        public static final String COLUMN_TYPE = "encounter_type";
        public static final String COLUMN_WORD = "encounter_word";
        public static final String COLUMN_USER_SCORE = "user_score";
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_CONTEXT = "context";

        public static final int TYPE_FLUENT = 0;
        public static final int TYPE_DECONSTRUCTED = 1;
        public static final int TYPE_INCORRECT = 2;
        public static final int TYPE_HESITATED = 3;

        public static final int CONTEXT_FLASHCARD = 0;
        public static final int CONTEXT_EXPECTED = 1;
        public static final int CONTEXT_LEFT_FLUENT = 2;
        public static final int CONTEXT_RIGHT_FLUENT = 3;
        public static final int CONTEXT_DUAL_FLUENT = 4;
    }
}