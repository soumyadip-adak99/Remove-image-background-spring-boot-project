import { assets, FOOTER_CONSTANTS } from "../assets/assets"

const Footer = () => {
    return (
        <footer className="flex flex-col sm:flex-row items-center justify-between gap-4 px-4 lg:px-44 py-6 bg-transperent border-t border-gray-200 text-center sm:text-left">
            {/* Logo */}
            <div className="flex items-center justify-center sm:justify-start w-full sm:w-auto">
                <img src={assets.logo} alt="logo" width={32} className="shrink-0" />
            </div>

            {/* Copyright */}
            <p className="text-gray-600 text-sm w-full sm:w-auto">
                &copy; {new Date().getFullYear()} @soumyadipadak | All rights reserved.
            </p>

            {/* Social Icons */}
            <div className="flex gap-3 justify-center sm:justify-end w-full sm:w-auto">
                {FOOTER_CONSTANTS.map((item, index) => (
                    <a
                        href={item.url}
                        key={index}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="transition-transform hover:scale-110"
                    >
                        <img src={item.logo} alt="social-icon" width={24} height={24} />
                    </a>
                ))}
            </div>
        </footer>
    )
}

export default Footer
