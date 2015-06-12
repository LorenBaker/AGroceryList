package com.lbconsulting.agrocerylist.classes;

/**
 * EventBus events.
 */
public class MyEvents {

    public static class updateUI {
        public updateUI() {
        }
    }

    public static class setActionBarTitle {
        final String mTitle;

        public setActionBarTitle(String title) {
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }

    public static class showOkDialog {
        final String mTitle;
        final String mMessage;

        public showOkDialog( String title, String message) {
            mTitle = title;
            mMessage = message;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class showToast {
        final String mMessage;

        public showToast( String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }
}
