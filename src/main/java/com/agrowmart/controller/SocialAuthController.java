// Author Vishal Sapkal 
//Date :- 10-12-2025
// this is not work because payment  for google console subscraption - 15 k 

package com.agrowmart.controller;



import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/social")
public class SocialAuthController {

 // This method automatically detects current server URL
 private String getBaseUrl(HttpServletRequest request) {
     String scheme = request.getScheme();             // http or https
     String serverName = request.getServerName();     // hostname (localhost, 192.168.x.x, yourdomain.com)
     int serverPort = request.getServerPort();        // 8080, 80, 443 etc.

     // Don't add port if it's default (80 for http, 443 for https)
     if ((scheme.equals("http") && serverPort == 80) || 
         (scheme.equals("https") && serverPort == 443)) {
         return scheme + "://" + serverName;
     }
     return scheme + "://" + serverName + ":" + serverPort;
 }

 @GetMapping("/login/google")
 public RedirectView googleLogin(HttpServletRequest request,
                                @RequestParam(required = false) String fcmToken) {
     String baseUrl = getBaseUrl(request);
     String url = baseUrl + "/oauth2/authorization/google";
     if (fcmToken != null && !fcmToken.isBlank()) {
         url += "?fcmToken=" + fcmToken;
     }
     return new RedirectView(url);
 }

 @GetMapping("/login/facebook")
 public RedirectView facebookLogin(HttpServletRequest request,
                                  @RequestParam(required = false) String fcmToken) {
     String baseUrl = getBaseUrl(request);
     String url = baseUrl + "/oauth2/authorization/facebook";
     if (fcmToken != null && !fcmToken.isBlank()) {
         url += "?fcmToken=" + fcmToken;
     }
     return new RedirectView(url);
 }
}