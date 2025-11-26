package com.nttdata.user_service.application.dto;

import com.nttdata.user_service.domain.model.Phone;
import lombok.Data;
import java.util.List;

@Data
public class UserRequest {
    private String name;
    private String email;
    private String password;
    private List<Phone> phones;
}