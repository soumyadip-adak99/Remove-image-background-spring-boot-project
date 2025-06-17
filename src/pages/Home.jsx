import BgRemovalSteps from "../components/BgRemovalSteps"
import BgSlider from "../components/BgSlider"
import Heder from "../components/Heder"
import Pricing from "../components/Pricing"
import Testimonials from "../components/Testimonials"
import TryNow from "../components/TryNow"

const Home = () => {
    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 font-['Outfit']">


            {/* {hero section} */}
            <Heder />

            {/* background removeval stpes */}
            <BgRemovalSteps />

            {/* background removeval slider section */}
            <BgSlider />

            {/* buy credit plan section */}
            <Pricing />

            {/* user testimonial section */}
            <Testimonials />

            {/* try now components */}
            <TryNow />
        </div >
    )
}

export default Home