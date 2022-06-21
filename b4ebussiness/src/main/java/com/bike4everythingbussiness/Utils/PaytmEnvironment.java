package com.bike4everythingbussiness.Utils;

//import com.citrus.sdk.Environment;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by vijay on 14/7/16.
 */
public enum PaytmEnvironment {



    LIVE {
        @Override
        public String getMID() {
            return "NbaTec92533507910524";
        }

        @Override
        public String getMerchantKey() {
            return "merchant";
        }  //rkY_z4WAbxmvIR%#

        @Override
        public String getWebsite() {
            return "NbaTecWAP";
        }

        @Override
        public String getChannelId() {
            return "WAP";
        }

        @Override
        public String getIndustryTypeId() {
            return "Retail109";
        }

        @Override
        public String getClientID() {
            return "merchant-nba-tech-solutions";
        }

        @Override
        public String getSecretKey() {
            return "17803a89-7cac-4edb-be91-8445a643e22c";
        }

        @Override
        public String getAuth() {
            byte[]   bytesEncoded = Base64.encodeBase64((getClientID()+":"+getSecretKey()).getBytes());
            String auth = "Basic " + new String(bytesEncoded );
            System.out.println("ecncoded value is " +auth);
            return auth;
        }

        @Override
        public String getWalletUrl() {
            return "https://secure.paytm.in/";
        }

        @Override
        public String getAccountUrl() {
            return "https://accounts.paytm.com/";
        }
    },
    DEMO {
        @Override
        public String getMID() {
            return "NBATec45564621192191";
        }

        @Override
        public String getMerchantKey() {
            return "merchant";
        }  //CKtfB5y403YiPcw1

        @Override
        public String getWebsite() {
            return "APP_STAGING";
        }

        @Override
        public String getChannelId() {
            return "WAP";
        }

        @Override
        public String getIndustryTypeId() {
            return "Retail";
        }

        @Override
        public String getClientID() {
            return "merchant-nba-tech-staging";
        }

        @Override
        public String getSecretKey() {
            return "";
        }

        @Override
        public String getAuth() {
            return "Basic bWVyY2hhbnQtbmJhLXRlY2gtc3RhZ2luZzoxMjUwZTg4NS0xNGY1LTQyODAtOWViYS02YjhkZWE4ZGQ3ZTk=";
        }

        @Override
        public String getWalletUrl() {
            return "https://pguat.paytm.com/";
        }

        @Override
        public String getAccountUrl() {
            return "https://accounts-uat.paytm.com/signin/";
        }
    };







    public abstract String getMID();
    public abstract String getMerchantKey();
    public abstract String getWebsite();
    public abstract String getChannelId();
    public abstract String getIndustryTypeId();
    public abstract String getClientID();
    public abstract String getSecretKey();
    public abstract String getAuth();
    public abstract String getWalletUrl();
    public abstract String getAccountUrl();
}
