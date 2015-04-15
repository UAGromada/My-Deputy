package com.zeus.android.mydeputy.app.api.request;

import com.zeus.android.mydeputy.app.model.Deputy;

/**
 * Created by admin on 2/23/15.
 */
public class DeputyInfoEditRequest extends BaseRequest {

    private Params params;

    public void setParams(Params params) {
        this.params = params;
    }

    public static class Params{

        private String full_name;
        private String party_name;
        private String program;
        private String promises;
        private Deputy.Contacts contacts;

        public void setFull_name(String full_name) {
            this.full_name = full_name;
        }

        public void setParty_name(String party_name) {
            this.party_name = party_name;
        }

        public void setProgramme(String programme) {
            this.program = programme;
        }

        public void setPromises(String promises) {
            this.promises = promises;
        }

        public void setContacts(Deputy.Contacts contacts) {
            this.contacts = contacts;
        }

    }
}
