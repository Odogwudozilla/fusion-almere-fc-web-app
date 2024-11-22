package com.fusionalmerefc.service;

import com.fusionalmerefc.model.User;
import com.fusionalmerefc.model.constants.Status;

import java.util.List;
import java.util.UUID;

public interface UserService extends BaseService<User, UUID> {
    List<User> findByStatus(Status status);
}
