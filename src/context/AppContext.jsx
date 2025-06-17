import { useAuth, useClerk, useUser } from "@clerk/clerk-react";
import axios from "axios";
import { createContext, useState } from "react";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";

export const AppContext = createContext();

const AppContextProvider = (props) => {
    const backendurl = import.meta.env.VITE_BACKEND_URL;
    const [credit, setCredit] = useState(0);
    const { getToken } = useAuth();
    const [image, setImage] = useState(false);
    const [resultImage, setResultImage] = useState(false);
    const { isSignedIn } = useUser();
    const { openSignIn } = useClerk();
    const navigate = useNavigate();

    const loadUserCredits = async () => {
        try {
            const token = await getToken();

            if (!token) return toast.error("Authentication token not available");
            if (!backendurl) return toast.error("Backend URL not configured");

            const response = await axios.get(`${backendurl}/api/users/credits`, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.data.success) {
                setCredit(response.data.data.credits);
            } else {
                toast.error(response.data.data || "Failed to load credits");
            }
        } catch (error) {
            console.error("Error loading user credits:", error);
            const status = error.response?.status;

            if (status === 401) toast.error("Authentication failed. Please sign in again.");
            else if (status === 403) toast.error("Access forbidden.");
            else if (status === 404) toast.error("User not found.");
            else if (error.request) toast.error("Network error.");
            else toast.error("An unexpected error occurred.");
        }
    };

    const removeBg = async (selectedImage) => {
        try {
            if (!isSignedIn) {
                await openSignIn();
                return;
            }

            if (credit === 0) {
                toast.error("Credit limit expired.");
                setImage(null);
                setResultImage(false);
                navigator("/")
                return;
            }

            setImage(selectedImage);
            setResultImage(false);
            navigate("/result");

            const token = await getToken();
            const formData = new FormData();
            if (selectedImage) formData.append("file", selectedImage);

            const { data: base64Image } = await axios.post(
                `${backendurl}/api/images/remove-background`,
                formData,
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setResultImage(`data:image/png;base64, ${base64Image}`);
            setCredit((prev) => Math.max(0, prev - 1));
        } catch (error) {
            navigator('/')
            console.error("Error from removeBg:", error);
            toast.error("Error removing background");
        }
    };

    return (
        <AppContext.Provider value={{
            credit, setCredit,
            backendurl,
            loadUserCredits,
            image, setImage,
            resultImage, setResultImage,
            removeBg
        }}>
            {props.children}
        </AppContext.Provider>
    );
};

export default AppContextProvider;