import { Routes, Route } from "react-router-dom";
import Footer from "./components/Footer";
import Menubar from "./components/Menubar";
import Home from "./pages/Home";
import { Toaster } from 'react-hot-toast';
import UserSyncHandler from "./components/UserSyncHandler";
import Connection from "./api/Connection";
import { SignedOut, SignedIn, RedirectToSignIn } from "@clerk/clerk-react";
import Result from "./pages/Result";
import PricingCatagory from "./pages/PricingCatagory";

function App() {
    return (
        <>
            <Connection />
            <UserSyncHandler />
            <Menubar />
            <Toaster />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/pricing" element={<PricingCatagory />} />
                <Route path="/result" element={
                    <>
                        <SignedIn>
                            <Result />
                        </SignedIn>
                        <SignedOut>
                            <RedirectToSignIn />
                        </SignedOut>
                    </>
                } />
            </Routes>
            <Footer />
        </>
    );
}

export default App;
