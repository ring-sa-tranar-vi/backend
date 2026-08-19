package dev.salt.Ring20.service.security;

import java.util.stream.Stream;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class DisplayResolverService {

    private static final String DEFAULT_DISPLAY_NAME = "No name entered";

    public String resolveDisplayName(Jwt jwt) {
        // Try common claim keys that Clerk/OpenID might provide for a user's name.

        String[] claimKeys = new String[] {"name", "full_name", "preferred_username"};
        for (String key : claimKeys) {
            Object claimVal = jwt.getClaims().get(key);
            if (claimVal instanceof String) {
                String s = ((String) claimVal).trim();
                if (!s.isEmpty()) return s;
            }
        }

        String givenName = jwt.getClaimAsString("given_name");
        String familyName = jwt.getClaimAsString("family_name");
        String fullName = getFullName(givenName, familyName);

        if (!fullName.isEmpty()) {
            return fullName;
        }

        String email = jwt.getClaimAsString("email");
        if (email != null && !email.isBlank()) {
            return email.trim();
        }

        // If Clerk does not provide a name claim, store a readable placeholder.
        return DEFAULT_DISPLAY_NAME;
    }

    private String getFullName(String givenName, String familyName) {
        return String.join(
                        " ",
                        Stream.of(givenName, familyName)
                                .filter(part -> part != null && !part.isBlank())
                                .toList())
                .trim();
    }
}
