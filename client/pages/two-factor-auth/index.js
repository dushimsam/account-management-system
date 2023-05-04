import React, { useEffect, useState } from "react";
import styles from "../../styles/modules/togglePage.module.css";
import { notifyError, notifySuccess } from "../../utils/alerts";
import { AuthService, UserService } from "../../services";
import { useSelector } from "react-redux";
import RouteService from "../../middlewares/routing";
import { KEYS, USER_CATEGORY } from "../../utils/constants";
import Router from "next/router";
import Auth from "../../middlewares/auth";

function TwoFactorAuthentication() {
  const [isEnabled, setIsEnabled] = useState(false);
  const [loading, setLoading] = useState(true);
  const authUser = useSelector((state) => state.authUser);

  const toggleAuthentication = () => {
    setIsEnabled(!isEnabled);
  };


  useEffect(async () => {
    if (!Auth.isActive()) {
        localStorage.setItem(KEYS.PREV_LINK_LOCAL_STORAGE_KEY, Router.asPath)
        await Router.push("/")
    }else
        setLoading(false)
}, [])


  if (loading) return <div/>

  const handleContinueToNextPage = async () => {
    
    if (RouteService?.getPrevRoute()) {
      let link = RouteService.getPrevRoute();
      RouteService.removePrevRoute();
      await Router.push(link);
    } else {
      if (authUser.category === USER_CATEGORY.ADMIN) {
        Router.push("/admin");
      } else {
        Router.push("/client");
      }
    }
  };

  const handleSet2Auth = async () => {
    try {
      const response = await UserService.set2Auth(authUser.id);
      if (response.status === 200) {
        notifySuccess(response.data.message);
        handleContinueToNextPage();
      }
    } catch (e) {
      notifyError(e.message);
    }
  };
  return (
    <div className={styles.container}>
      <h1>Enable Two-Factor Authentication</h1>
      <label className={styles["toggle-label"]}>Enable:</label>
      <div
        className={`${styles.toggle} ${isEnabled ? styles.on : styles.off}`}
        onClick={toggleAuthentication}
      >
        <div className={styles.switch} />
      </div>
      <label className={styles["toggle-label"]}>Disable:</label>
      <div className="page-wrapper mt-4">
        <div className="">
          <button
            className="btn-primary btn-sm"
            disabled={!isEnabled}
            onClick={() => handleSet2Auth()}
          >
            OK
          </button>
        </div>
        <div className="mt-4">
          <a href="#" onClick={()=>handleContinueToNextPage()}>Remind me later</a>
        </div>
      </div>
    </div>
  );
}

export default TwoFactorAuthentication;
