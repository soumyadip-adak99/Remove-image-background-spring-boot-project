import { useContext, useEffect, useRef } from 'react'
import { AppContext } from '../context/AppContext'
import axios from 'axios'
import toast from 'react-hot-toast'

const Connection = () => {
    const backendUrl = "http://localhost:8088/api/health-check"
    const pollInterval = 5000
    const intervalRef = useRef(null)
    const toastShownRef = useRef(false)

    useEffect(() => {
        const checkConnection = async () => {
            try {
                const response = await axios.get(backendUrl)
                if (response.status === 200) {
                    if (!toastShownRef.current) {
                        toast.success("Network connection successful.")
                        toastShownRef.current = true
                    }
                    // Clear interval when connection is successful
                    if (intervalRef.current) {
                        clearInterval(intervalRef.current)
                        intervalRef.current = null
                    }
                    return
                } else {
                    // Reset success flag if connection fails
                    toastShownRef.current = false
                    toast.error("Network connection failed.")
                }
            } catch (error) {
                // Reset success flag if there's an error
                toastShownRef.current = false
                console.log(error)
                toast.error("Network connection failed.")
            }
        }

        // Initial check
        checkConnection()

        // Set up polling
        intervalRef.current = setInterval(checkConnection, pollInterval)

        // Cleanup function
        return () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current)
                intervalRef.current = null
            }
        }
    }, [backendUrl])

    return null // This component doesn't render anything
}

export default Connection