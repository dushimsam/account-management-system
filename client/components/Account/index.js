import { useRouter } from "next/router"
import { useState, useEffect } from "react";

import jwt from 'jwt-decode';
import { useSelector } from "react-redux";
import globalStyles from "../../styles/global-colors.module.css"
import { displayDate } from "../../utils/displayDate";
import auth from "../../middlewares/auth";
import userService from "../../services/user.service";
import { VERIFICATION_STATUS } from "../../utils/constants";

export default function AccountDetails({ link, user }) {
    
    const router = useRouter();
    return (
        <div className="container-fluid">
            <div className="card py-4 pl-4 pr-5 shadow">
                <div className="row">
                    <div className="col-sm-6 text-left pb-3 pb-sm-0">
                        <div className="row">
                            <div className="col-4">
                                
                                <img
                                    id={"imageContainer"}
                                    className="nav-bar-avatar rounded-circle shadow"
                                    src={user.profileUrl}
                                    height={150}
                                    width={150}
                                    onError={(e) => {
                                        e.target.onerror = null;
                                        e.target.src =
                                            "https://ui-avatars.com/api/?name=" +
                                            user.firstName;
                                    }}
                                    alt={user.firstName}
                                    title={user.firstName}
                                />
                            
                            </div>
                            <div className="col-12 col-md-8 text-left mt-4">
                                <h3 className="font-weight-bold text-secondary">{`${user.firstName} ${user.lastName}`}</h3>
                                <h6 className="text-secondary">{`${user.gender}`}</h6>
                                <h6 className="text-secondary">{user.nationality}</h6>
                            </div>
                        </div>
                    </div>
                    <div className="col-sm-6 text-right pb-3 pb-sm-0">
                        <h6 className="text-secondary">{`${user.email}`}</h6>
                        <h6 className="text-secondary mt-4">Born at {`${displayDate(user.dateOfBirth, false)}`}</h6>
                        
                        <div className="change-profile mt-5">
                            <button className={`btn px-4 py-2 font-weight-bold ${user.verificationStatus == VERIFICATION_STATUS.UNVERIFIED ? 'btn-outline-danger': user.verificationStatus == VERIFICATION_STATUS.VERIFIED ? 'btn-outline-success' : 'btn-outline-warning'} `}
                                    disabled={true}> {user.verificationStatus}
                            </button>
                        </div>

                    </div>
                </div>
            </div>
            <h6 className="text-secondary mt-4">Last updated your account {`${displayDate(user.lastModifiedAt)}`}</h6>
        </div>
    )
}