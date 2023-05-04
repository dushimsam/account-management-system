//Import necessarry components

import { useEffect, useState } from "react";
import {
  KEYS,
  USER_CATEGORY,
  VERIFICATION_STATUS,
} from "../../../utils/constants";
import Auth from "../../../middlewares/auth";
import { useSelector } from "react-redux";
import NotFound from "../../404";
import DashboardLayout from "../../../layouts/DashboardLayout";
import { UserService } from "../../../services";
import { notifyError } from "../../../utils/alerts";
import AdminBody from "../../../components/AdminBody";
import Router from "next/router";

const App = () => {
  const [loading, setLoading] = useState(true);
  const [currPage, setCurrPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [users, setUsers] = useState([]);

  const getUsers = async () => {
    try {
      let res = await UserService.get_all_by_status_paginated(
        VERIFICATION_STATUS.UNVERIFIED,
        currPage
      );
      setUsers([...users, ...res.data.users]);
      setTotalPages(res.data.numberOfPages);
    } catch (e) {
      notifyError(e.message);
    }
  };

  useEffect(async () => {
    if (!Auth.isActive()) {
      localStorage.setItem(KEYS.PREV_LINK_LOCAL_STORAGE_KEY, Router.asPath);
      await Router.push("/");
    } else {
      setLoading(false);
      getUsers();
    }
  }, []);

  useEffect(() => {
    getUsers();
  }, [currPage]);

  if (loading) return <div />;

  return (
    <DashboardLayout userCategory={USER_CATEGORY.ADMIN}>
      <div className="container">
        <div className="row justify-content-center pt-2" id={"accountDetails"}>
          <div className="col-8">
            <h3 className="font-weight-bold">
              {" "}
              USERS' ACCOUNT MANAGEMENT SYSTEM DASHBOARD
            </h3>
          </div>
          <div className="col-11 mt-5">
            <AdminBody
              users={users}
              getUsers={getUsers}
              totalPages={totalPages}
              setCurrPage={setCurrPage}
              currPage={currPage}
            />
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default App;
