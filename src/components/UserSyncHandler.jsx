import { useAuth, useUser } from "@clerk/clerk-react";
import { useContext, useEffect, useState, useRef } from "react";
import { AppContext } from "../context/AppContext";
import axios from "axios";
import toast from "react-hot-toast";

const UserSyncHandler = () => {
    const { isLoaded, isSignedIn, getToken } = useAuth()
    const { user } = useUser();
    const [isLoading, setIsLoading] = useState(false)

    const { backendurl, loadUserCredits } = useContext(AppContext)
    const lastSyncedUserData = useRef(null)

    const createUserData = (user) => {
        return {
            clerkId: user.id,
            email: user.primaryEmailAddress?.emailAddress || "",
            firstName: user.firstName || "",
            lastName: user.lastName || "",
            photoUrl: user.imageUrl || "",
        }
    }

    const hasUserDataChanged = (newData, oldData) => {
        if (!oldData) return true;
        return (
            newData.email !== oldData.email ||
            newData.firstName !== oldData.firstName ||
            newData.lastName !== oldData.lastName ||
            newData.photoUrl !== oldData.photoUrl
        )
    }

    useEffect(() => {
        const syncUser = async () => {
            if (!isLoaded || !isSignedIn || !user || isLoading) return

            const userData = createUserData(user);
            if (!hasUserDataChanged(userData, lastSyncedUserData.current)) {
                await loadUserCredits()
                return
            }

            try {
                setIsLoading(true);
                const token = await getToken()
                if (!token) {
                    toast.error("Authentication token missing.");
                    return;
                }

                const response = await axios.post(
                    `${backendurl || "http://localhost:8088"}/api/users`,
                    userData,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json",
                        },
                    }
                )

                if (response.data?.success) {
                    lastSyncedUserData.current = userData
                    toast.success("User synced successfully.")
                    await loadUserCredits()
                } else {
                    toast.error(response.data?.data || "Sync failed.")
                }
            } catch (error) {
                console.error("Error syncing user:", error)
                if (error.response) {
                    const status = error.response.status
                    if (status === 401) {
                        toast.error("Authentication failed during sync.")
                    } else if (status === 403) {
                        toast.error("Access forbidden during sync.")
                    } else {
                        toast.error("Server error during sync.")
                    }
                } else {
                    toast.error("Network error during sync.")
                }
            } finally {
                setIsLoading(false)
            }
        }

        syncUser();
    }, [isLoaded, isSignedIn, user, getToken, backendurl, loadUserCredits])

    return null
}

export default UserSyncHandler