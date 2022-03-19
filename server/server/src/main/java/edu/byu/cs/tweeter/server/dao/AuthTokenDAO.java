package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTokenDAO extends DAO {
    AuthToken createToken();
    void deleteToken(AuthToken token);
}
