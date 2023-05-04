//Import necessarry components

import React, { useRef, useState, useEffect } from "react";
import Validator from "validatorjs";

import Head from "next/head";
import Link from "next/link";

import styles from "../../styles/modules/auth.module.scss";
import { AuthService, UserService } from "../../services";

import { useRouter } from "next/router";
import { APP_DETAILS, KEYS } from "../../utils/constants";
import { notifyError, notifySuccess } from "../../utils/alerts";
import Auth from "../../middlewares/auth";
import ForbiddenPage from "../../layouts/ForbiddenPage";
import { ImageContainer } from "../../components/widgets/ImageContainer";
import { Spinner } from "react-bootstrap";

export default function Register() {
  const [loading, setLoading] = useState(false);

  // Initialize the useRouter hook to navigate between pages
  const router = useRouter();

  // Initialize the useRef hook to obtain the value of input fields
  const firstNameContainer = useRef(null);
  const lastNameContainer = useRef(null);
  const emailContainer = useRef(null);
  const nationalityContainer = useRef(null);
  const passwordContainer = useRef(null);
  const genderContainer = useRef(null);
  const maritalStatusContainer = useRef(null);
  const dateOfBirthContainer = useRef(null);

  // Set up initial errors as an empty object
  const initialErrors = {
    firstName: [],
    lastName: [],
    email: [],
    dateOfBirth: [],
    password: [],
    gender: [],
    maritalStatus: [],
    nationality: [],
  };

  const [file, setFile] = useState(null);

  // Set up a state variable to track validation errors
  const [errors, setErrors] = useState(initialErrors);

  const onSelect = (selectedList) => {
    setSelectedClubs(selectedList);
  };

  // Use the useEffect hook to check if the user is already authenticated
  useEffect(() => {
    const fetchData = async () => {
      try {
        let is_authed = await Auth.isAuthed();
        if (is_authed) return await router.push("/");
      } catch (e) {}
    };
    fetchData().then();
  }, []);

  // Define a helper function to extract the value of an input field
  const getValue = (container) => container.current.value;

  const handleFileChange = (e) => {
    e.preventDefault();
    setFile(e.target.files[0]);
  };

  // Define a function to handle form submission
  const submitForm = async (event) => {
    try {
      setLoading(true);

      // Prevent the default form submission behavior
      event.preventDefault();

      // Retrieve the login and password values from the input fields
      const user = {
        firstName: getValue(firstNameContainer),
        lastName: getValue(lastNameContainer),
        email: getValue(emailContainer),
        maritalStatus: getValue(maritalStatusContainer),
        nationality: getValue(nationalityContainer),
        password: getValue(passwordContainer),
        gender: getValue(genderContainer),
        dateOfBirth: getValue(dateOfBirthContainer),
      };

      // Use the Validator library to validate the user object against certain rules
      let valid = new Validator(user, {
        email: "required|email",
        firstName: "required|string|min:3",
        lastName: "required|string|min:3",
        password: "required|string|min:6",
        dateOfBirth: "required",
        nationality: "required",
        maritalStatus: "required",
        gender: "required",
      });

      // If there are any validation errors, update the errors state variable
      if (valid.fails(undefined))
        return setErrors((errors) => {
          return { ...errors, ...valid.errors.all() };
        });

      // If there are no validation errors, attempt to register the user
      if (valid.passes(undefined)) {
        setErrors((errors) => {
          return { ...errors, ...valid.errors.all() };
        });

        const res = await UserService.create(user);

        if (file) {
          const formData = new FormData();
          formData.append("file", file);
          const res2 = await UserService.uploadProfilePicture(
            res.data.id,
            formData
          );
        }

        notifySuccess("User registered successfully");

        // After registering the user, redirect them to the login page
        await router.push("/");
      }
    } catch (e) {
      // setErrors({ ...initialErrors, ...e.response.data });
      notifyError(e.response.data.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <ForbiddenPage>
      <Head>
        <title>Register - {APP_DETAILS.NAME_FULL}</title>
      </Head>
      <div className="row mx-0">
        <div className="col-5 col-md-4 _bg-primary  font-weight-bolder text-white text-center">
          <div
            className={`${styles.right_bar} d-flex flex-column align-items-center justify-content-center px-3`}
          >
            <h1 className="pb-4 font-weight-bolder">Welcome back !</h1>
            <p className="font-weight-light">
              To keep connected with us <br /> provide with us your info
            </p>
            <Link href={`/`}>
              <button className="btn btn-side border border-white text-white px-5 py-2 my-5 rounded-pill">
                SIGN IN
              </button>
            </Link>
          </div>
        </div>
        <div className="col-md-8 col-7 justify-content-center">
          <div className="container">
            <div className="row justify-content-center">
              <div className="col-4 mt-4">
                <h1 className="font-weight-bolder _color-primary pb-4">
                  Create Account
                </h1>
              </div>
            </div>
            <div className="row">
              <div className="col-12">
                <form
                  className="row justify-content-center justify-content-md-between"
                  onSubmit={submitForm}
                >
                  <div className="form-group col-md-6">
                    <label htmlFor="first_name">First Name</label>
                    <input
                      type="text"
                      id="first_name"
                      ref={firstNameContainer}
                      className={`form-control ${
                        errors.firstName.length > 0 && "is-invalid"
                      } _input`}
                    />
                    <div className="invalid-feedback">
                      {errors.firstName[0]}
                    </div>
                  </div>
                  <div className="form-group col-md-6">
                    <label htmlFor="lastName">Last Name</label>
                    <input
                      type="text"
                      id="lastName"
                      ref={lastNameContainer}
                      className={`form-control ${
                        errors.lastName.length > 0 && "is-invalid"
                      } _input`}
                    />
                    <div className="invalid-feedback">{errors.lastName[0]}</div>
                  </div>
                  <div className="form-group col-md-6">
                    <label htmlFor="email">Email</label>
                    <input
                      type="email"
                      id="email"
                      ref={emailContainer}
                      className={`form-control ${
                        errors.email.length > 0 && "is-invalid"
                      } _input`}
                    />
                    <div className="invalid-feedback">{errors.email[0]}</div>
                  </div>

                  <div className="form-group col-md-6">
                    <label htmlFor="nationality">Nationality</label>
                    <select
                      type="select"
                      id="nationality"
                      className={`form-control ${
                        errors.nationality != "" && "is-invalid"
                      } _input`}
                      ref={nationalityContainer}
                    >
                      <option value=""></option>
                      <option value="AFGHAN">Afghan</option>
                      <option value="ALBANIAN">Albanian</option>
                      <option value="ALGERIAN">Algerian</option>
                      <option value="AMERICAN">American</option>
                      <option value="ANDORRAN">Andorran</option>
                      <option value="ANGOLAN">Angolan</option>
                      <option value="ANTIGUANS">Antiguans</option>
                      <option value="ARGENTINEAN">Argentinean</option>
                      <option value="ARMENIAN">Armenian</option>
                      <option value="AUSTRALIAN">Australian</option>
                      <option value="AUSTRIAN">Austrian</option>
                      <option value="AZERBAIJANI">Azerbaijani</option>
                      <option value="BAHAMIAN">Bahamian</option>
                      <option value="BAHRAINI">Bahraini</option>
                      <option value="BANGLADESHI">Bangladeshi</option>
                      <option value="BARBADIAN">Barbadian</option>
                      <option value="BARBUDANS">Barbudans</option>
                      <option value="BATSWANA">Batswana</option>
                      <option value="BELARUSIAN">Belarusian</option>
                      <option value="BELGIAN">Belgian</option>
                      <option value="BELIZEAN">Belizean</option>
                      <option value="BENINESE">Beninese</option>
                      <option value="BHUTANESE">Bhutanese</option>
                      <option value="BOLIVIAN">Bolivian</option>
                      <option value="BOSNIAN">Bosnian</option>
                      <option value="BRAZILIAN">Brazilian</option>
                      <option value="BRITISH">British</option>
                      <option value="BRUNEIAN">Bruneian</option>
                      <option value="BULGARIAN">Bulgarian</option>
                      <option value="BURKINABE">Burkinabe</option>
                      <option value="BURMESE">Burmese</option>
                      <option value="BURUNDIAN">Burundian</option>
                      <option value="CAMBODIAN">Cambodian</option>
                      <option value="CAMEROONIAN">Cameroonian</option>
                      <option value="CANADIAN">Canadian</option>
                      <option value="CAPE_VERDEAN">Cape Verdean</option>
                      <option value="CENTRAL_AFRICAN">Central African</option>
                      <option value="CHADIAN">Chadian</option>
                      <option value="CHILEAN">Chilean</option>
                      <option value="CHINESE">Chinese</option>
                      <option value="COLOMBIAN">Colombian</option>
                      <option value="COMORAN">Comoran</option>
                      <option value="CONGOLESE">Congolese</option>
                      <option value="COSTA_RICAN">Costa Rican</option>
                      <option value="CROATIAN">Croatian</option>
                      <option value="CUBAN">Cuban</option>
                      <option value="CYPRIOT">Cypriot</option>
                      <option value="CZECH">Czech</option>
                      <option value="DANISH">Danish</option>
                      <option value="DJIBOUTI">Djibouti</option>
                      <option value="DOMINICAN">Dominican</option>
                      <option value="DUTCH">Dutch</option>
                      <option value="EAST_TIMORESE">East Timorese</option>
                      <option value="ECUADOREAN">Ecuadorean</option>
                      <option value="EGYPTIAN">Egyptian</option>
                      <option value="EMIRIAN">Emirian</option>
                      <option value="EQUATORIAL_GUINEAN">
                        Equatorial Guinean
                      </option>
                      <option value="ERITREAN">Eritrean</option>
                      <option value="ESTONIAN">Estonian</option>
                      <option value="ETHIOPIAN">Ethiopian</option>
                      <option value="FIJIAN">Fijian</option>
                      <option value="FILIPINO">Filipino</option>
                      <option value="FINNISH">Finnish</option>
                      <option value="FRENCH">French</option>
                      <option value="GABONESE">Gabonese</option>
                      <option value="GAMBIAN">Gambian</option>
                      <option value="GEORGIAN">Georgian</option>
                      <option value="GERMAN">German</option>
                      <option value="GHANAIAN">Ghanaian</option>
                      <option value="GREEK">Greek</option>
                      <option value="GRENADIAN">Grenadian</option>
                      <option value="GUATEMALAN">Guatemalan</option>
                      <option value="GUINEA_BISSAUAN">Guinea-Bissauan</option>
                      <option value="GUINEAN">Guinean</option>
                      <option value="GUYANESE">Guyanese</option>
                      <option value="HAITIAN">Haitian</option>
                      <option value="HERZEGOVINIAN">Herzegovinian</option>
                      <option value="HONDURAN">Honduran</option>
                      <option value="HUNGARIAN">Hungarian</option>
                      <option value="ICELANDER">Icelander</option>
                      <option value="INDIAN">Indian</option>
                      <option value="INDONESIAN">Indonesian</option>
                      <option value="IRANIAN">Iranian</option>
                      <option value="IRAQI">Iraqi</option>
                      <option value="IRISH">Irish</option>
                      <option value="ISRAELI">Israeli</option>
                      <option value="ITALIAN">Italian</option>
                      <option value="IVORIAN">Ivorian</option>
                      <option value="JAMAICAN">Jamaican</option>
                      <option value="JAPANESE">Japanese</option>
                      <option value="JORDANIAN">Jordanian</option>
                      <option value="KAZAKHSTANI">Kazakhstani</option>
                      <option value="KENYAN">Kenyan</option>
                      <option value="KITTIAN_AND_NEVISIAN">
                        Kittian and Nevisian
                      </option>
                      <option value="KUWAITI">Kuwaiti</option>
                      <option value="KYRGYZ">Kyrgyz</option>
                      <option value="LAOTIAN">Laotian</option>
                      <option value="LATVIAN">Latvian</option>
                      <option value="LEBANESE">Lebanese</option>
                      <option value="LIBERIAN">Liberian</option>
                      <option value="LIBYAN">Libyan</option>
                      <option value="LIECHTENSTEINER">Liechtensteiner</option>
                      <option value="LITHUANIAN">Lithuanian</option>
                      <option value="LUXEMBOURGER">Luxembourger</option>
                      <option value="MACEDONIAN">Macedonian</option>
                      <option value="MALAGASY">Malagasy</option>
                      <option value="MALAWIAN">Malawian</option>
                      <option value="MALAYSIAN">Malaysian</option>
                      <option value="MALDIVAN">Maldivan</option>
                      <option value="MALIAN">Malian</option>
                      <option value="MALTESE">Maltese</option>
                      <option value="MARSHALLESE">Marshallese</option>
                      <option value="MAURITANIAN">Mauritanian</option>
                      <option value="MAURITIAN">Mauritian</option>
                      <option value="MEXICAN">Mexican</option>
                      <option value="MICRONESIAN">Micronesian</option>
                      <option value="MOLDOVAN">Moldovan</option>
                      <option value="MONACAN">Monacan</option>
                      <option value="MONGOLIAN">Mongolian</option>
                      <option value="MOROCCAN">Moroccan</option>
                      <option value="MOSOTHO">Mosotho</option>
                      <option value="MOTSWANA">Motswana</option>
                      <option value="MOZAMBICAN">Mozambican</option>
                      <option value="NAMIBIAN">Namibian</option>
                      <option value="NAURUAN">Nauruan</option>
                      <option value="NEPALESE">Nepalese</option>
                      <option value="NEW_ZEALANDER">New Zealander</option>
                      <option value="NI_VANUATU">Ni-Vanuatu</option>
                      <option value="NICARAGUAN">Nicaraguan</option>
                      <option value="NIGERIEN">Nigerien</option>
                      <option value="NORTH_KOREAN">North Korean</option>
                      <option value="NORTHERN_IRISH">Northern Irish</option>
                      <option value="NORWEGIAN">Norwegian</option>
                      <option value="OMANI">Omani</option>
                      <option value="PAKISTANI">Pakistani</option>
                      <option value="PALAUAN">Palauan</option>
                      <option value="PANAMANIAN">Panamanian</option>
                      <option value="PAPUA_NEW_GUINEAN">
                        Papua New Guinean
                      </option>
                      <option value="PARAGUAYAN">Paraguayan</option>
                      <option value="PERUVIAN">Peruvian</option>
                      <option value="POLISH">Polish</option>
                      <option value="PORTUGUESE">Portuguese</option>
                      <option value="QATARI">Qatari</option>
                      <option value="ROMANIAN">Romanian</option>
                      <option value="RUSSIAN">Russian</option>
                      <option selected value="RWANDAN">
                        Rwandan
                      </option>
                      <option value="SAINT_LUCIAN">Saint Lucian</option>
                      <option value="SALVADORAN">Salvadoran</option>
                      <option value="SAMOAN">Samoan</option>
                      <option value="SAN_MARINESE">San Marinese</option>
                      <option value="SAO_TOMEAN">Sao Tomean</option>
                      <option value="SAUDI">Saudi</option>
                      <option value="SCOTTISH">Scottish</option>
                      <option value="SENEGALESE">Senegalese</option>
                      <option value="SERBIAN">Serbian</option>
                      <option value="SEYCHELLOIS">Seychellois</option>
                      <option value="SIERRA_LEONEAN">Sierra Leonean</option>
                      <option value="SINGAPOREAN">Singaporean</option>
                      <option value="SLOVAKIAN">Slovakian</option>
                      <option value="SLOVENIAN">Slovenian</option>
                      <option value="SOLOMON_ISLANDER">Solomon Islander</option>
                      <option value="SOMALI">Somali</option>
                      <option value="SOUTH_AFRICAN">South African</option>
                      <option value="SOUTH_KOREAN">South Korean</option>
                      <option value="SPANISH">Spanish</option>
                      <option value="SRI_LANKAN">Sri Lankan</option>
                      <option value="SUDANESE">Sudanese</option>
                      <option value="SURINAMER">Surinamer</option>
                      <option value="SWAZI">Swazi</option>
                      <option value="SWEDISH">Swedish</option>
                      <option value="SWISS">Swiss</option>
                      <option value="SYRIAN">Syrian</option>
                      <option value="TAIWANESE">Taiwanese</option>
                      <option value="TAJIK">Tajik</option>
                      <option value="TANZANIAN">Tanzanian</option>
                      <option value="THAI">Thai</option>
                      <option value="TOGOLESE">Togolese</option>
                      <option value="TONGAN">Tongan</option>
                      <option value="TRINIDADIAN_OR_TOBAGONIAN">
                        Trinidadian or Tobagonian
                      </option>
                      <option value="TUNISIAN">Tunisian</option>
                      <option value="TURKISH">Turkish</option>
                      <option value="TUVALUAN">Tuvaluan</option>
                      <option value="UGANDAN">Ugandan</option>
                      <option value="UKRAINIAN">Ukrainian</option>
                      <option value="URUGUAYAN">Uruguayan</option>
                      <option value="UZBEKISTANI">Uzbekistani</option>
                      <option value="VENEZUELAN">Venezuelan</option>
                      <option value="VIETNAMESE">Vietnamese</option>
                      <option value="WELSH">Welsh</option>
                      <option value="YEMENITE">Yemenite</option>
                      <option value="ZAMBIAN">Zambian</option>
                      <option value="ZIMBABWEAN">Zimbabwean</option>
                    </select>

                    <div className="invalid-feedback">
                      {errors.nationality[0]}
                    </div>
                  </div>
                  <div className="form-group col-md-6">
                    <label htmlFor="maritalStatus">Marital Status</label>
                    <select
                      type="select"
                      id="maritalStatus"
                      className={`form-control ${
                        errors.maritalStatus != "" && "is-invalid"
                      } _input`}
                      ref={maritalStatusContainer}
                    >
                      <option value=""></option>
                      <option value="SINGLE">Single</option>
                      <option value="MARRIED">Married</option>
                      <option value="DIVORCED">Divorced</option>
                    </select>
                    <div className="invalid-feedback">
                      {errors.maritalStatus[0]}
                    </div>
                  </div>

                  <div className="form-group col-md-6">
                    <label htmlFor="lastName">Date of Birth</label>
                    <input
                      type="date"
                      id="dateOfBirth"
                      ref={dateOfBirthContainer}
                      className={`form-control ${
                        errors.dateOfBirth.length != "" && "is-invalid"
                      } _input`}
                    />
                    <div className="invalid-feedback">
                      {errors.dateOfBirth[0]}
                    </div>
                  </div>

                  <div className="form-group col-md-6">
                    <label htmlFor="gender">Gender</label>
                    <select
                      type="select"
                      id="gender"
                      className={`form-control ${
                        errors.gender != "" && "is-invalid"
                      } _input`}
                      ref={genderContainer}
                    >
                      <option value=""></option>
                      <option value={"MALE"}>Male</option>
                      <option value={"FEMALE"}>Female</option>
                    </select>
                    <div className="invalid-feedback">{errors.gender[0]}</div>
                  </div>

                  <div className="form-group col-md-6">
                    <label htmlFor="password">Password</label>
                    <input
                      type="password"
                      id="password"
                      ref={passwordContainer}
                      className={`form-control  ${
                        errors.password.length > 0 && "is-invalid"
                      } _input`}
                    />
                    <div className="invalid-feedback">{errors.password[0]}</div>
                  </div>

                  <div className="form-group col-md-6">
                    <div className="">
                      <ImageContainer file={file} status={"CREATE"} />
                    </div>
                    <div className="">
                      <label
                        htmlFor="dob"
                        className="block text-sm font-medium text-gray-700"
                      >
                        Profile pic
                      </label>
                      <input
                        onChange={handleFileChange}
                        type="file"
                        accept="image/*"
                        hidden
                        id="filePicker"
                      />
                      <div
                        onClick={(e) => {
                          document.getElementById("filePicker")?.click();
                        }}
                        className="uploadAttachment cursor-pointer"
                      >
                        <div className="mr-2">
                          <svg
                            style={{ cursor: "pointer" }}
                            width="35"
                            height="30"
                            viewBox="0 0 29 24"
                            fill="none"
                            xmlns="http://www.w3.org/2000/svg"
                          >
                            <path
                              d="M8.38028 20.9808C6.74499 20.8759 5.19118 20.3421 3.94071 19.4556C2.69025 18.5692 1.80707 17.3753 1.41729 16.0446C1.02751 14.7139 1.15105 13.3143 1.77028 12.0457C2.38951 10.7772 3.47275 9.70445 4.86535 8.98076C5.16167 7.05085 6.28976 5.27727 8.03851 3.99192C9.78725 2.70658 12.0368 1.99756 14.3662 1.99756C16.6956 1.99756 18.9451 2.70658 20.6939 3.99192C22.4426 5.27727 23.5707 7.05085 23.867 8.98076C25.2596 9.70445 26.3429 10.7772 26.9621 12.0457C27.5813 13.3143 27.7049 14.7139 27.3151 16.0446C26.9253 17.3753 26.0421 18.5692 24.7917 19.4556C23.5412 20.3421 21.9874 20.8759 20.3521 20.9808V20.9998H8.38028V20.9808ZM15.5634 12.9998H19.1549L14.3662 7.99976L9.57746 12.9998H13.169V16.9998H15.5634V12.9998Z"
                              fill="#1679A8"
                            />
                          </svg>
                        </div>
                        <div>{file ? file.name : "Pick a file"}</div>
                      </div>
                    </div>
                  </div>
                  <div className="text-center col-12">
                    <button
                      type="submit"
                      className="btn _btn text-white px-5 py-2 my-4 rounded-pill"
                      disabled={loading}
                    >
                      {loading ? (
                        <Spinner
                          as="span"
                          animation="border"
                          size="sm"
                          role="status"
                          aria-hidden="true"
                        />
                      ) : (
                        "SIGN UP"
                      )}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </ForbiddenPage>
  );
}
