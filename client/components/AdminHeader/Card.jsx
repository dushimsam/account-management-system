import styles from "../../styles/components/card.module.css"

export default function Card({ color, label="", value=0, icon }) {
    const getStyles = () => {
        switch (color) {
            case "primary":
                return styles.iconPrimary;
            case "success":
                return styles.iconSuccess;
            case "danger":
                return styles.iconDanger;
            case "warning":
                return styles.iconWarning;
            case "info":
                return styles.iconInfo
            default:
                break;
        }
    }

    return (
        <div className={styles.container + " py-3 px-4 d-flex justify-content-between align-items-center"}>
            <div>
                <div className={"icon p-3 rounded-circle " +getStyles()}>
                    {icon}
                </div>
            </div>
            <div className="text-right">
                <h5 className="font-weight-bolder">{value.toLocaleString()}</h5>
                <div className={"text-secondary " + styles.label}>{label}</div>
            </div>
        </div>
    )
}