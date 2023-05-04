//Import necessarry components
import React, { useState, useRef, useEffect } from "react";
import Head from "next/head";
import Link from "next/link";
import styles from "../../styles/modules/auth.module.scss";
import Validator from "validatorjs";
import { AuthService } from "../../services";
import Router, { useRouter } from "next/router";
import { APP_DETAILS, KEYS, USER_CATEGORY } from "../../utils/constants";
import { notifyError, notifySuccess } from "../../utils/alerts";
import RouteService from "../../middlewares/routing";
import Auth from "../../middlewares/auth";
import ForbiddenPage from "../../layouts/ForbiddenPage";
import jwt from "jwt-decode";
import { Spinner } from "react-bootstrap";

export default function ResetPassword() {
  // Initialize the useRouter hook to navigate between pages
  const router = useRouter();

  const [loading, setLoading] = useState(false);

  // Initialize the useRef hook to obtain the value of input fields
  const confirmPasswordContainer = useRef(null);
  const passWordContainer = useRef(null);

  const token = router.query.token;

  // Set up initial errors as an empty object
  const initialErrors = {
    newPassword: [],
    confirmPassword: [],
  };

  // Set up a state variable to track validation errors
  const [errors, setErrors] = useState(initialErrors);

  // Define a helper function to extract the value of an input field
  const getValue = (container) => container.current.value;

  // Define a function to handle form submission
  const submitForm = async (event) => {
    try {
      setLoading(true)
      if (!token) {
        notifyError("Invalid token");
        await Router.back();
      } else {
        // Prevent the default form submission behavior
        event.preventDefault();
        // Retrieve the login and password values from the input fields
        const body = {
          newPassword: getValue(passWordContainer),
          confirmPassword: getValue(confirmPasswordContainer),
        };

        // Use the Validator library to validate the user object against certain rules
        let valid = new Validator(body, {
          newPassword: "required|min:4",
          confirmPassword: "required|min:4",
        });

        // If there are any validation errors, update the errors state variable
        if (valid.fails(undefined))
          return setErrors({ ...initialErrors, ...valid.errors.all() });

        // If there are no validation errors, attempt to log in the user
        if (valid.passes(undefined)) {
          setErrors(initialErrors);
          let data = await AuthService.resetPassword(token, body);

          // If a token is received, set it in the Auth utility and notify the user of successful login
          if (data.data.token) {
            Auth.setToken(data.data.token);
            notifySuccess("Password Reset successfully");

            // If there is a previously stored route, navigate the user there
            if (RouteService?.getPrevRoute()) {
              let link = RouteService.getPrevRoute();
              RouteService.removePrevRoute();
              await Router.push(link);
            } else {
              if (jwt(data.data.token).User.category == USER_CATEGORY.CLIENT)
                window.location.href = "/client";
              else window.location.href = "/admin";
            }
          }
        }
      }
    } catch (e) {
      notifyError(e.response?.data.message);
    }finally{
      setLoading(false)
    }
  };

  return (
    <ForbiddenPage>
      <Head>
        <title>Reset password - {APP_DETAILS.NAME_FULL} </title>
      </Head>
      <div className="row mx-0">
        <div className="col-lg-12 col-7">
          <div className="vh-100 d-md-flex flex-md-column align-items-center justify-content-center mt-5 mt-lg-0">
            <h1 className="font-weight-bolder _color-primary pb-5">
              Reset password of your account
            </h1>
            <form action="" onSubmit={submitForm}>
              <div className="form-group">
                <label htmlFor="password">New Password</label>
                <input
                  type="password"
                  ref={passWordContainer}
                  id="password"
                  className={`form-control col-12 ${
                    errors.newPassword.length > 0 && "is-invalid"
                  } _input`}
                />
                <div className="invalid-feedback">{errors.newPassword[0]}</div>
              </div>
              <div className="form-group">
                <label htmlFor="password">Confirm Password</label>
                <input
                  type="password"
                  ref={confirmPasswordContainer}
                  id="password"
                  className={`form-control col-12 ${
                    errors.confirmPassword.length > 0 && "is-invalid"
                  } _input`}
                />
                <div className="invalid-feedback">
                  {errors.confirmPassword[0]}
                </div>
              </div>
              <div className="text-center">
                <button
                  type="submit"
                  className="btn _btn text-white px-5 py-2 my-4 rounded-pill"
                  disabled={loading}
                >{
                  loading ? (
                    <Spinner
                    as="span"
                    animation="border"
                    size="sm"
                    role="status"
                    aria-hidden="true"
                  />
                  ): ("Reset Password")
                }
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </ForbiddenPage>
  );
}
