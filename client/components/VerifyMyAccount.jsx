import { useEffect, useRef, useState } from "react";
import UploadDragAndDrop from "./widgets/UploadDragDrop";
import { UserService, VerificationService } from "../services";
import { notifyError, notifySuccess } from "../utils/alerts";
import { useSelector } from "react-redux";
import Validator from "validatorjs";
import { VERIFICATION_STATUS } from "../utils/constants";

const CompleteSteps = ({
  errors,
  submitForm,
  setPassportOrNid,
  passportOrNid,
}) => {

  return (
    <div>
          <form action="" onSubmit={submitForm}>
            <div className="form-group">
              <label
                htmlFor="nidOrPassport"
                className="h5 font-weight-bold mt-4"
              >
                NID or Passport Number
              </label>
              <input
                type="text"
                style={{ fontSize: "1.1em", width: "100%" }}
                value={passportOrNid}
                onChange={(e) => setPassportOrNid(e.target.value)}
                id="nidOrPassport"
                className={`form-control ${
                  errors.nidOrPassport.length > 5 && "is-invalid"
                } _input`}
              />
              <div className="invalid-feedback">{errors.nidOrPassport[0]}</div>
            </div>
            <div className="text-center">
              <button
                type="submit"
                className="btn _btn text-white px-5 py-2 my-4 rounded-pill"
              >
                DONE
              </button>
            </div>
          </form>
    </div>
  );
};

const VerifyMyAccount = ({ user, setUser}) => {
  const [file, setFile] = useState(null);

  const [passportOrNid, setPassportOrNid] = useState("");

  const initialErrors = {
    nidOrPassport: [],
  };

  useEffect(()=>{
    if(user && Object.keys(user).length > 0 && user.verificationStatus != VERIFICATION_STATUS.UNVERIFIED){
        setFile(user.accountVerifications[user.accountVerifications.length -1].officialDocument)
    }
  },[user])

  // Set up a state variable to track validation errors
  const [errors, setErrors] = useState(initialErrors);

  // Define a function to handle form submission
  const submitForm = async (event) => {
    try {
      // Prevent the default form submission behavior
      event.preventDefault();
      // Retrieve the login and password values from the input fields
      let req = {
        nidOrPassport: passportOrNid,
      };

      // Use the Validator library to validate the user object against certain rules
      let valid = new Validator(req, {
        nidOrPassport: "required|min:6",
      });

      // If there are any validation errors, update the errors state variable
      if (valid.fails(undefined))
        return setErrors({ ...initialErrors, ...valid.errors.all() });

      //   // If there are no validation errors, attempt to log in the user
      if (valid.passes(undefined)) {
        setErrors(initialErrors);

        // check if file is not uploaded
        if (file == null) {
          notifyError("Please upload your document");
        }else if(!req.nidOrPassport || req.nidOrPassport ===''){
          notifyError("Please enter your Nid or Password number");
        }else {
          req.userId = user.id;

          // Send a POST request to the server to request the user account verification
          const res = await VerificationService.create(req);

          // // upload the file
          const formData = new FormData();
          formData.append("file", file);
          const res2 = await VerificationService.upload(res.data.id, formData);

          // update the user details by fetching it again
          const userRes = await UserService.get_by_id(user.id);
          setUser(userRes.data);

          notifySuccess(res2.data.message);
        }
      }else{
        notifyError("Please enter your Nid or Password number");
      }
    } catch (e) {
      notifyError(e.response?.data.message);
      console.log(e)
    }
  };

  useEffect(() => {
    console.log(file);
  }, [file]);

  return (
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-10">
          <UploadDragAndDrop file={file} setFile={setFile} status={user.verificationStatus} />
        </div>
        <div className="col-10">
          {file != null && user.verificationStatus == VERIFICATION_STATUS.UNVERIFIED ? (
            <CompleteSteps
              submitForm={submitForm}
              errors={errors}
              setPassportOrNid={setPassportOrNid}
              passportOrNid={passportOrNid}
            />
          ) : (
            <></>
          )}
        </div>
      </div>
    </div>
  );
};

export default VerifyMyAccount;
