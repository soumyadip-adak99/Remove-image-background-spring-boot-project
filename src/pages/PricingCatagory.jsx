import { useAuth, useClerk } from "@clerk/clerk-react"
import { plans } from "../assets/assets"
import { placeOrder } from "../service/OrderService"
import { useContext, useState } from "react"
import { AppContext } from "../context/AppContext"

const PricingCatagory = () => {
    const { isSignedIn, getToken } = useAuth()
    const { openSignIn } = useClerk()
    const { loadUserCredits, backendurl } = useContext(AppContext)
    const [isProcessing, setIsProcessing] = useState(false)

    const handleOrder = async (planId) => {
        if (!isSignedIn) {
            return openSignIn()
        }

        setIsProcessing(true)
        try {
            await placeOrder({
                planId,
                getToken,
                onSuccess: () => {
                    loadUserCredits()
                },
                backendurl
            })
        } finally {
            setIsProcessing(false)
        }
    }

    return (
        <div className="py-10 md:px-20 lg:px-20">
            <div className="container mx-auto px-4">
                {/* section title */}
                <div className="mb-12 text-center">
                    <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-12 text-center">
                        Choose your perfect package
                    </h2>
                </div>

                <p className="mx-auto mt-4 max-w-2xl text-center px-4 text-gray-500 text-base sm:text-lg mb-16">
                    Select from our carefully curated photography packages designed to meet your specific needs and budget.
                </p>

                {/* section body */}
                <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
                    {plans.map((plan) => (
                        <div
                            key={plan.id}
                            className={`relative pt-6 p-6 bg-[#1A1A1A] ${plan.popular
                                ? 'backdrop-blur-lg rounded-2xl'
                                : 'border border-gray-800 rounded-xl'
                                } hover:-translate-y-2 transition-all duration-300 shadow-lg`}
                        >
                            {plan.popular && (
                                <div className="absolute -top-4 left-1/2 -translate-x-1/2 rounded-full bg-purple-600 px-3 py-1 text-white text-sm font-semibold">
                                    Most Popular
                                </div>
                            )}

                            <div className="text-center p-6">
                                <h3 className="text-2xl font-bold text-white">{plan.name}</h3>
                                <div className="mt-4 text-center">
                                    <span className="text-4xl text-violet-400 font-bold">
                                        ₹{plan.price}
                                    </span>
                                </div>
                            </div>

                            <div className="px-4 pb-6">
                                <ul className="mb-6 space-y-4">
                                    <li className="text-white">{plan.credits} credits</li>
                                    <li className="text-white">{plan.description}</li>
                                </ul>
                                <button
                                    className={`w-full py-3 px-6 text-center text-white font-semibold rounded-full bg-gradient-to-r from-purple-500 to-indigo-500 shadow-lg hover:from-purple-600 hover:to-indigo-600 transition duration-300 ease-in-out transform hover:scale-105 ${isProcessing ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
                                        }`}
                                    onClick={() => handleOrder(plan.id)}
                                    disabled={isProcessing}
                                >
                                    {isProcessing ? 'Processing...' : 'Choose Plan'}
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}


export default PricingCatagory;