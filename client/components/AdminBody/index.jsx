import { useState } from "react";
import { VERIFICATION_STATUS } from "../../utils/constants";
import AppUserProfile from "../Profile";
import styles from "../../styles/components/table.module.css";
import $ from "jquery";

const AdminBody = ({ users, totalPages, setCurrPage, currPage, getUsers }) => {
  const [selectedUser, setSelectedUser] = useState(null);

  const toggleModal = () => {
    $(function () {
      $("#profileModalDetails").modal("show");
    });
  };

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-12 table-responsive">
          <table className="table">
            <thead>
              <tr className="font-weight-bold">
                <th scope="col">#</th>
                <th scope="col">Names</th>
                <th scope="col">Status</th>
                <th scope="col">Email</th>
                <th scope="col">Gender</th>
                <th scope="col">Marital Status</th>
                <th scope="col">Age</th>
                <th scope="col">Actions</th>
              </tr>
            </thead>
            <tbody className="sm:flex-1 sm:flex-none">
              {users.length == 0 && (
                <div className="row justify-content-center pt-5">
                  <div className="col-5">No users available </div>
                </div>
              )}
              {users.map((user, index) => {
                return (
                  <tr key={user.id}>
                    <td>{index + 1}</td>
                    <td className="flex">
                      {user.firstName} {user.lastName}{" "}
                    </td>
                    <td className={styles.td}>
                      <span
                        className={
                          user.verificationStatus ===
                          VERIFICATION_STATUS.VERIFIED
                            ? styles.active
                            : user.verificationStatus ===
                              VERIFICATION_STATUS.UNVERIFIED
                            ? styles.inactive
                            : user.verificationStatus ===
                              VERIFICATION_STATUS.PENDING_VERIFICATION
                            ? styles.pending
                            : ""
                        }
                      >
                        {user.verificationStatus ===
                        VERIFICATION_STATUS.VERIFIED ? (
                          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="18" height="18"><path d="M11.602 13.7599L13.014 15.1719L21.4795 6.7063L22.8938 8.12051L13.014 18.0003L6.65 11.6363L8.06421 10.2221L10.189 12.3469L11.6025 13.7594L11.602 13.7599ZM11.6037 10.9322L16.5563 5.97949L17.9666 7.38977L13.014 12.3424L11.6037 10.9322ZM8.77698 16.5873L7.36396 18.0003L1 11.6363L2.41421 10.2221L3.82723 11.6352L3.82604 11.6363L8.77698 16.5873Z" fill="rgba(255,255,255,1)"></path></svg>
                        ) : (
                          user.verificationStatus
                        )}
                      </span>
                    </td>
                    <td>{user.email}</td>
                    <td>{user.gender}</td>
                    <td>{user.maritalStatus}</td>
                    <td>
                      {parseInt(new Date().getFullYear()) -
                        parseInt(new Date(user.dateOfBirth).getFullYear())}
                    </td>
                    <td className="pt-1 p-3">
                      <div className="flex">
                        <button
                          onClick={() => {
                            setSelectedUser({ ...user });
                            toggleModal();
                          }}
                          className="btn btn-rounded btn-icon mr-2"
                        >
                          <svg
                            style={{ cursor: "pointer" }}
                            xmlns="http://www.w3.org/2000/svg"
                            fill={"#707070"}
                            viewBox="0 0 24 24"
                            width="18"
                            height="18"
                          >
                            <path fill="none" d="M0 0h24v24H0z" />
                            <path d="M12 3c5.392 0 9.878 3.88 10.819 9-.94 5.12-5.427 9-10.819 9-5.392 0-9.878-3.88-10.819-9C2.121 6.88 6.608 3 12 3zm0 16a9.005 9.005 0 0 0 8.777-7 9.005 9.005 0 0 0-17.554 0A9.005 9.005 0 0 0 12 19zm0-2.5a4.5 4.5 0 1 1 0-9 4.5 4.5 0 0 1 0 9zm0-2a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5z" />
                          </svg>
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          {selectedUser && <AppUserProfile UserObj={selectedUser} setSelectedUser={setSelectedUser} getUsers={getUsers}/>}
        </div>
      </div>
      {currPage + 1 < totalPages && (
        <div className="row justify-content-center">
          <div className="col-3">
            <div className={` py-3 `}>
              <button
                className="btn bg-white shadow-lg font-weight-bold"
                on
                onClick={() => setCurrPage(currPage + 1)}
              >
                See more
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 24 24"
                  width="24"
                  height="24"
                >
                  <path fill="none" d="M0 0h24v24H0z" />
                  <path d="M12 13.172l4.95-4.95 1.414 1.414L12 16 5.636 9.636 7.05 8.222z" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminBody;
