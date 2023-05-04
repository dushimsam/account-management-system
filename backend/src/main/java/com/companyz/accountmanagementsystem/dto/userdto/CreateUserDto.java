package com.companyz.accountmanagementsystem.dto.userdto;
import com.companyz.accountmanagementsystem.enums.Gender;
import com.companyz.accountmanagementsystem.enums.MaritalStatus;
import com.companyz.accountmanagementsystem.enums.Nationality;
import com.companyz.accountmanagementsystem.enums.UserCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Email
    private String email;

    @NotNull
    private Date dateOfBirth;

    @NotNull
    private String password;

    @NotNull
    private Gender gender;

    @NotNull
    private MaritalStatus maritalStatus;

    @NotNull
    private UserCategory category = UserCategory.CLIENT;

    private Nationality nationality = Nationality.RWANDAN;
}
