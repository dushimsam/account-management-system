//Import necessarry components
import AccountDetails from "../../components/Account";
import { useEffect, useState } from 'react';
import { KEYS, USER_CATEGORY } from '../../utils/constants';
import Auth from "../../middlewares/auth";
import { useSelector } from "react-redux";
import DashboardLayout from "../../layouts/DashboardLayout";
import VerifyMyAccount from "../../components/VerifyMyAccount";
import  Router  from "next/router";

const App = () => {
    const authUser = useSelector(state => state.authUser)
    const [user,setUser] = useState(null);
    

    useEffect(()=>{
        if(authUser)
            setUser(authUser)
    },[authUser])

    
  return (
      <DashboardLayout userCategory={USER_CATEGORY.CLIENT}>
          <div className='container'>
              <div className='row justify-content-center pt-2' id={"accountDetails"}>
                  <div className='col-12'>
                      <AccountDetails user={authUser}/>
                  </div>
                  <div className='col-12'>
                    <VerifyMyAccount user = {user} setUser={setUser}/>
                  </div>
              </div>
          </div>
      </DashboardLayout>
  );
};

export default App;