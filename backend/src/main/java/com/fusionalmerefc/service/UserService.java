package com.fusionalmerefc.service;

import com.fusionalmerefc.model.Status;
import com.fusionalmerefc.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService extends BaseService<User, UUID> {
    List<User> findByStatus(Status status);
}
