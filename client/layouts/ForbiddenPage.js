import { useSelector } from "react-redux";
import React, { useEffect, useState } from "react";
import Router from "next/router";
import Auth from "../middlewares/auth";
import { USER_CATEGORY } from "../utils/constants";

export const ForbiddenPage = ({ children }) => {
  const authUser = useSelector((state) => state.authUser);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const isActive = Auth.isActive();
    if (isActive) {
      if(authUser.category == USER_CATEGORY.ADMIN)
        Router.push("/admin");
      else if(authUser.category == USER_CATEGORY.CLIENT)
        Router.push("/client");
    }else{
      setLoading(false);
    }
  }, [authUser]);

  if(loading) return ""

  return <>{children}</>;
};

export default ForbiddenPage;
