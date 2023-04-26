import React from "react";


function AppMeta() {
 
  return (
    <Head>
      <title>Account Management System</title>
      <meta name={"description"} content={"Account management system"} />
      <meta property={"og:title"} content={"Social Media"} />
      <meta name="keywords" content="accounts, nid, irembo, system-design" />
      <meta name="author" content="SAMUEL DUSHIMIMANA" />
      <meta property="og:description" content={"Manage your account"} />
    </Head>
  );
}

function MyApp({ Component, pageProps }) {
  return (
    <Provider store={store}>
      <Toaster reverseOrder={false} />
      <AppMeta />
      <Component {...pageProps} />
    </Provider>
  );
}

export default MyApp;
