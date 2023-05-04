//Import necessarry components
import React, { useState, useRef, useEffect } from "react";
import Head from "next/head";
import Link from "next/link";
import styles from "../styles/modules/auth.module.scss";
import Validator from "validatorjs";
import { AuthService } from "../services";
import Router, { useRouter } from "next/router";
import { APP_DETAILS, KEYS, USER_CATEGORY } from "../utils/constants";
import { notifyError, notifySuccess } from "../utils/alerts";
import RouteService from "../middlewares/routing";
import Auth from "../middlewares/auth";
import ForbiddenPage from "../layouts/ForbiddenPage";
import jwt from "jwt-decode";
import { Spinner } from "react-bootstrap";


const ForgotPasswordPage = ({submitForm, resetEmailContainer, loading, setPageStatus }) =>{
  return(
    <div className="container">
    <div className="row justify-content-center">
      <div className="7">
        <div className="text-center">
          <h2>Please enter Email address</h2>
          <p>
            Enter your email address to receive reset password link:
          </p>
        </div>
      </div>
      <div className="col-10 pt-3">
        <form action="" onSubmit={submitForm}>
          <div className="form-group">
            <label htmlFor="token">Email</label>
            <input
              type="text"
              ref={resetEmailContainer}
              id="token"
              className={`form-control col-12`}
            />
          </div>
          <div className="text-center">
            <button
              type="submit"
              className="btn _btn text-white px-5 py-2 my-4 rounded-pill"
              disabled={loading}
            >
              {loading ? (
                <Spinner
                  as="span"
                  animation="border"
                  size="sm"
                  role="status"
                  aria-hidden="true"
                />
              ) : (
                "SEND LINK"
              )}
            </button>
            <div><a className="btn text-primary" onClick={()=>setPageStatus('LOGIN')}>Back to login</a></div>
          </div>
        </form>
      </div>
    </div>
  </div>
  )
}
const LoginPage = ({
  submitForm,
  emailContainer,
  passWordContainer,
  loading,
  errors,
  setPageStatus
}) => {
  return (
    <>
      <h1 className="font-weight-bolder _color-primary pb-5">
        Log in into your account
      </h1>
      <form action="" onSubmit={submitForm}>
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="text"
            ref={emailContainer}
            id="login"
            className={`form-control col-12 ${
              errors.email.length > 0 && "is-invalid"
            } _input`}
          />
          <div className="invalid-feedback">{errors.email[0]}</div>
        </div>
        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            ref={passWordContainer}
            id="password"
            className={`form-control col-12 ${
              errors.password.length > 0 && "is-invalid"
            } _input`}
            
          />
          <div className="invalid-feedback">{errors.password[0]}</div>
        </div>
        <div className="text-center">
          <button
            type="submit"
            className="btn _btn text-white px-5 py-2 my-4 rounded-pill"
            disabled={loading}
          >
            {loading ? (
              <Spinner
                as="span"
                animation="border"
                size="sm"
                role="status"
                aria-hidden="true"
              />
            ) : (
              "SIGN IN"
            )}
          </button>
          <div><a className="btn text-primary" onClick={()=>setPageStatus('FORGOT_PASS')}>forgot password ?</a></div>
        </div>
      </form>
    </>
  );
};
const TokenPage = ({ tokenContainer, submitToken, loading, setPageStatus }) => {
  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="7">
          <div className="text-center">
            <h2>Please enter 2FA code</h2>
            <p>
              Use your Two factor Authentication code sent on your email to
              proceed login process
            </p>
          </div>
        </div>
        <div className="col-10 pt-3">
          <form action="" onSubmit={submitToken}>
            <div className="form-group">
              <label htmlFor="token">Code</label>
              <input
                type="text"
                ref={tokenContainer}
                id="token"
                className={`form-control col-12`}
              />
            </div>
            <div className="text-center">
              <button
                type="submit"
                className="btn _btn text-white px-5 py-2 my-4 rounded-pill"
                disabled={loading}
              >
                {loading ? (
                  <Spinner
                    as="span"
                    animation="border"
                    size="sm"
                    role="status"
                    aria-hidden="true"
                  />
                ) : (
                  "Verify"
                )}
              </button>
              <div><a className="btn text-primary" onClick={()=>setPageStatus('LOGIN')}>Back to login</a></div>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default function Login() {
  // Initialize the useRouter hook to navigate between pages
  const router = useRouter();

  // Initialize the useRef hook to obtain the value of input fields
  const emailContainer = useRef(null);
  const passWordContainer = useRef(null);
  const resetEmailContainer = useRef(null);
  const tokenContainer = useRef(null);

  // Set up initial errors as an empty object
  const initialErrors = {
    email: [],
    password: [],
  };

  // Set up a state variable to track validation errors
  const [errors, setErrors] = useState(initialErrors);

  const [pageStatus, setPageStatus] = useState("LOGIN");

  // Define a helper function to extract the value of an input field
  const getValue = (container) => container.current.value;

  const [loading, setLoading] = useState(false);


  // Define a function to handle form submission
  const submitForm = async (event) => {
    try {
      setLoading(true);
      // Prevent the default form submission behavior
      event.preventDefault();
      // Retrieve the login and password values from the input fields
      const user = {
        email: getValue(emailContainer),
        password: getValue(passWordContainer),
      };

      // Use the Validator library to validate the user object against certain rules
      let valid = new Validator(user, {
        email: "required|min:4",
        password: "required|string|min:8",
      });

      // If there are any validation errors, update the errors state variable
      if (valid.fails(undefined))
        return setErrors({ ...initialErrors, ...valid.errors.all() });

      // If there are no validation errors, attempt to log in the user
      if (valid.passes(undefined)) {
        setErrors(initialErrors);
        let data = await AuthService.login(user);

        // If a token is received, set it in the Auth utility and notify the user of successful login
        if (data.data.token) {
          if (data.data.extra == "2FA") {
            notifySuccess("Sent 2FA code to your email address");
            setPageStatus("VERIFY_TOKEN");
          } else {
            Auth.setToken(data.data.token);
            notifySuccess("Logged in successfully");
            window.location.href="/two-factor-auth";
          }
        }
      }
    } catch (e) {
      // Handle any errors that may occur during the login process
      if (e.response?.status === 400)
        return setErrors({ ...initialErrors, ...e.response.data });
      notifyError(e.response?.data.message);
    } finally {
      setLoading(false);
    }
  };

  const submitToken = async (event) => {
    try {
      setLoading(true);
      event.preventDefault();
      const token = getValue(tokenContainer);
      if (token.length < 1) {
        notifyError("Please enter 2FA code");
      } else {
        let data = await AuthService.verifyToken(token);
        if (data.data.token) {
          Auth.setToken(data.data.token);
          notifySuccess("Logged in successfully");

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
    } catch (e) {
      notifyError(e.response?.data.message);
    } finally {
      setLoading(false);
    }
  };

  const SubmitResetReq = async(event) =>{
    try{
     setLoading(true);
     event.preventDefault();
     const email = getValue(resetEmailContainer);
     if (email.length < 1) {
       notifyError("Please enter your email");
     }else{
       let {data} = await AuthService.generateResetPasswordToken(email);
       notifySuccess(data.message)
       await Router.push("/");
     }
    }catch(e) {
      notifyError(e.response?.data.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <ForbiddenPage>
      <Head>
        <title>Log in - {APP_DETAILS.NAME_FULL} </title>
      </Head>
      <div className="row mx-0">
        <div className="col-lg-8 col-7">
          <div className="vh-100 d-md-flex flex-md-column align-items-center justify-content-center mt-5 mt-lg-0">
            {pageStatus == "VERIFY_TOKEN" ? (
              <TokenPage
                submitToken={submitToken}
                tokenContainer={tokenContainer}
                loading={loading}
                setPageStatus={setPageStatus}
              />
            ) : pageStatus == "LOGIN" ? (
              <LoginPage
                loading={loading}
                submitForm={submitForm}
                emailContainer={emailContainer}
                passWordContainer={passWordContainer}
                errors={errors}
                setPageStatus={setPageStatus}
              />
            ): <ForgotPasswordPage loading={loading} submitForm = {SubmitResetReq} resetEmailContainer={resetEmailContainer} setPageStatus={setPageStatus}/>}
          </div>
        </div>
        <div className="col-lg-4 col-5 _bg-primary px-0 font-weight-bolder text-white text-center">
          <div
            className={`${styles.right_bar} d-md-flex flex-md-column align-items-center mt-5 mt-lg-0 justify-content-center`}
          >
            <h1 className="pb-4 font-weight-bolder">Hello, Friend !</h1>
            <p className="font-weight-light">
              Enter your credentials to start <br /> journey with us
            </p>
            <Link href={"/register"}>
              <button className="btn btn-side border border-white text-white px-5 py-2 my-5 rounded-pill">
                SIGN UP
              </button>
            </Link>
          </div>
        </div>
      </div>
    </ForbiddenPage>
  );
}
