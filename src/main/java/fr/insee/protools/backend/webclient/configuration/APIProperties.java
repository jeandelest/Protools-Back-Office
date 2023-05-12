package fr.insee.protools.backend.webclient.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class APIProperties {

   private String url;
   private AuthProperties auth;
   private Boolean enabled=Boolean.FALSE;
   @Data
   @NoArgsConstructor
   //Infos to retrieve an oath token
   public static class AuthProperties {
      private String url;
      private String realm;
      private String clientId;
      private String clientSecret;

      public AuthProperties(String url, String realm, String clientId, String clientSecret) {
         this.url = url;
         this.realm = realm;
         this.clientId = clientId;
         this.clientSecret = clientSecret;
      }

      @Override
      public String toString() {
         return "AuthProperties{" +
                 "url='" + url + '\'' +
                 ", realm='" + realm + '\'' +
                 ", clientId='" + clientId + '\'' +
                 '}';
      }
   }

}