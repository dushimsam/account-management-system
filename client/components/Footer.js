import styles from "../styles/Home.module.scss";
import { APP_DETAILS } from "../utils/constants";

export const  Footer = () =>(
    <footer className={styles.footer}>
    <div className={`row ${styles.divs}`}>
        <div className="col-12 col-md-4">
            <div className={styles.logo}>
                {APP_DETAILS.NAME}
            </div>
        </div>
        <div className="col-12 col-md-6 d-block d-md-flex">
            <div className={styles.link}>SERVICES</div>
            <div className={styles.link}>BENEFITS</div>
            <div className={styles.link}>GET IN TOUCH</div>
        </div>
        <div className="col-12 col-md-3 mt-4 mt-md-0">
            {/* eslint-disable-next-line @next/next/no-img-element */}
     </div>
    </div>
    <hr className={styles.hr} />
    <div className={`row ${styles.divs}`}>
        <div className="col-12 col-md-9">
            <div className={styles.smallLink}>
            {`${new Date().getFullYear()} ${APP_DETAILS.NAME} by Samuel Dushimimana`}
            </div>
        </div>
        <div className="col-12 col-md-3 d-block d-md-flex justify-content-end">
            <div className={styles.smallLink}>Terms Or Services</div>
            <div className={styles.smallLink}>Privacy & Policies</div>
        </div>
    </div>
</footer>)

export  default  Footer;
