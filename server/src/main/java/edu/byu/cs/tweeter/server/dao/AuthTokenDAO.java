package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTokenDAO extends DAO {
    AuthToken createToken(String alias);
    void deleteToken(AuthToken token);
    boolean checkAuthToken(AuthToken token);
//    void deleteTokens(AuthToken token);
//    String getUserWToken(AuthToken token);
}
