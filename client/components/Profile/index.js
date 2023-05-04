import { Spinner } from "react-bootstrap";
import { VERIFICATION_STATUS } from "../../utils/constants";
import { displayDate } from "../../utils/displayDate";
import styles from "../../styles/components/profile-details.module.css";
import UploadDragAndDrop from "../widgets/UploadDragDrop";
import { useState } from "react";
import { UserService, VerificationService } from "../../services";
import { notifyError, notifySuccess } from "../../utils/alerts";

const AppUserProfile = ({ UserObj, setSelectedUser, getUsers }) => {
  const [loading, setLoading] = useState(false);

  const handleVerify = async () => {
    try {
      setLoading(true);
      await VerificationService.verify(
        UserObj.accountVerifications[UserObj.accountVerifications.length - 1]
          ?.id
      );

      const res = await UserService.get_by_id(UserObj.id);
      getUsers();
      setSelectedUser(res.data);
      notifySuccess("Account verified successfully");
    } catch (e) {
      notifyError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="modal fade bd-example-modal-lg"
      id="profileModalDetails"
      tabIndex="-1"
      role="dialog"
      aria-labelledby="myLargeModalLabel"
      aria-hidden="true"
    >
      <div className="modal-dialog modal-dialog-centered modal-lg">
        <div className={"modal-content"}>
          <div className={"container"}>
            <div className="mb-3 row mx-0 px-5 py-md-5 py-2">
              <div className={"col-md-4 col-12 " + styles.cardBody}>
                <div className="d-flex flex-column">
                  <img
                    id={"imageContainer"}
                    src={UserObj?.profileUrl}
                    className="rounded-circle shadow-sm"
                    width="100"
                    onError={(e) => {
                      e.target.onerror = null;
                      e.target.src =
                        "https://ui-avatars.com/api/?name=" + UserObj.firstName;
                    }}
                    alt={UserObj.firstName}
                    title={UserObj.firstName}
                  />

                  <div className="mt-3">
                    <h4>{`${UserObj.firstName} ${UserObj.lastName}`}</h4>
                    <p className="text-secondary mb-1">{UserObj.category}</p>
                    <p className="text-muted font-size-sm">
                      {displayDate(UserObj.createdAt)}
                    </p>
                    {/* <button className="btn btn-outline-primary">Message</button> */}
                    <button
                      style={
                        UserObj.verificationStatus ===
                        VERIFICATION_STATUS.PENDING_VERIFICATION
                          ? { cursor: "pointer" }
                          : { cursor: "not-allowed" }
                      }
                      className={
                        "btn text-white " +
                        (UserObj.verificationStatus ===
                        VERIFICATION_STATUS.UNVERIFIED
                          ? "btn-danger"
                          : UserObj.verificationStatus ===
                            VERIFICATION_STATUS.VERIFIED
                          ? "btn-success"
                          : "btn-warning")
                      }
                      onClick={() => {
                        handleVerify();
                      }}
                      data-toggle="modal"
                      data-target="#userConfirmationModal"
                      disabled={
                        UserObj.verificationStatus !==
                        VERIFICATION_STATUS.PENDING_VERIFICATION
                      }
                    >
                      {loading ? (
                        <Spinner
                          as="span"
                          animation="border"
                          size="sm"
                          role="status"
                          aria-hidden="true"
                        />
                      ) : UserObj.verificationStatus ===
                        VERIFICATION_STATUS.PENDING_VERIFICATION ? (
                        "VERIFIY"
                      ) : UserObj.verificationStatus ===
                        VERIFICATION_STATUS.UNVERIFIED ? (
                        "UNVERIFIED"
                      ) : (
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 24 24"
                          width="18"
                          height="18"
                        >
                          <path
                            d="M11.602 13.7599L13.014 15.1719L21.4795 6.7063L22.8938 8.12051L13.014 18.0003L6.65 11.6363L8.06421 10.2221L10.189 12.3469L11.6025 13.7594L11.602 13.7599ZM11.6037 10.9322L16.5563 5.97949L17.9666 7.38977L13.014 12.3424L11.6037 10.9322ZM8.77698 16.5873L7.36396 18.0003L1 11.6363L2.41421 10.2221L3.82723 11.6352L3.82604 11.6363L8.77698 16.5873Z"
                            fill="rgba(255,255,255,1)"
                          ></path>
                        </svg>
                      )}
                    </button>
                  </div>
                </div>
              </div>

              <div className="col-md-8 col-12">
                <div className="row justify-content-center">
                  <div className={"card mt-3 col-12 p-3"}>
                    <ul className="list-group list-group-flush">
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0">Full Names</h6>
                        <span className="text-secondary">{`${UserObj.firstName} ${UserObj.lastName}`}</span>
                      </li>
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0">Email</h6>
                        <span className="text-secondary">{`${UserObj.email}`}</span>
                      </li>
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0">Marital Status</h6>
                        <span className="text-secondary">{`${UserObj.maritalStatus}`}</span>
                      </li>
                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0">Nationality</h6>
                        <span className="text-secondary">{`${UserObj.nationality}`}</span>
                      </li>
                      {UserObj.verificationStatus !==
                        VERIFICATION_STATUS.UNVERIFIED && (
                        <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                          <h6 className="mb-0">National ID or Passport</h6>
                          <span className="text-secondary">{`${
                            UserObj.accountVerifications[
                              UserObj?.accountVerifications.length - 1
                            ]?.nidOrPassport
                          }`}</span>
                        </li>
                      )}

                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0">Date of Birth</h6>
                        <span className="text-secondary">{`${displayDate(
                          UserObj.dateOfBirth, false
                        )}`}</span>
                      </li>

                      <li className="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                        <h6 className="mb-0">Gender</h6>
                        <span className="text-secondary">{`${
                          UserObj.gender || `N/A`
                        }`}</span>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
          {(UserObj.verificationStatus ===
            VERIFICATION_STATUS.PENDING_VERIFICATION ||
            UserObj.verificationStatus === VERIFICATION_STATUS.VERIFIED) && (
            <div className="row justify-content-center pb-5">
              <div className="col-6">
                <h4 className="font-weight-bold">Proof of verification</h4>
              </div>
              <div className="col-10">
                <UploadDragAndDrop
                  file={
                    UserObj.accountVerifications[
                      UserObj.accountVerifications.length - 1
                    ].officialDocument
                  }
                  status={VERIFICATION_STATUS.verificationStatus}
                />
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AppUserProfile;
