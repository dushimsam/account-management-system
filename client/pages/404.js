import Head from 'next/head'
import Router from 'next/router';
import {APP_DETAILS} from "../utils/constants";

export default function NotFound() {
    return (
        <div>
            <Head>
                <title>Page not found | {APP_DETAILS.NAME}</title>
                <link rel="icon" href={"/favicon.ico"} />
            </Head>
            <div className="container-fluid bg-white px-3 px-sm-4 py-5 px-md-5" style={{height: '100vh'}}>
                <div className="px-0 py-0 px-sm-2 px-md-4 px-md-5">
                    <div className="row">
                        <div className="col-sm-4 my-auto">
                            <p className="display-2 font-weight-bolder text-app-red">
                                <span style={{ borderBottom: "10px solid #51288d" }}>Opp</span>s!
                        </p>
                            <h3 className="mt-5 pb-2 text-secondary">We can't find the page you are looking for.</h3>
                        </div>
                        <div className="col-sm-7 mx-auto">
                            <img className="w-100 d-block" src={"/images/imageedit_6_9080572701.png"} alt="404 error" />
                        </div>
                    </div>
                    <div className="text-center mt-5">
                        <button className={"btn  btn-lg col-8 col-sm-6 col-md-4"} style={{backgroundColor: "#51288d", color: 'white', fontSize: '15px', textTransform: 'uppercase'}} onClick={() => Router.push('/')}>Go to home</button>
                    </div>
                </div>
            </div>
        </div>
    )
}