package com.studyhard.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegisterRequest {
  @NotBlank(message = "{user.username.notblank}")
  @Size(min = 3,max = 50,message = "{user.username.size}")
  String username;
  @NotBlank(message = "{user.password.notblank}")
  @Size(min = 3,max = 50,message = "{user.password.size}")
  String password;
  @NotBlank(message = "{user.email.notblank}")
  String email;
}
