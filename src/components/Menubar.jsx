import { useContext, useState } from 'react';
import { assets } from '../assets/assets';
import { Menu, X } from 'lucide-react';
import { Link, NavLink } from 'react-router-dom';
import { SignedOut, SignedIn, useClerk, UserButton, useUser } from '@clerk/clerk-react';
import { AppContext } from '../context/AppContext';

const Menubar = () => {
    const [menuOpen, setMenuOpen] = useState(false);
    const { openSignIn, openSignUp } = useClerk();
    const { user } = useUser();
    const { credit } = useContext(AppContext)

    const openRegister = () => {
        setMenuOpen(false);
        openSignUp({});
    };

    const openLogin = () => {
        setMenuOpen(false);
        openSignIn({});
    };



    return (
        <nav className='bg-transparent px-6 py-4 sm:px-8 sm:py-6 flex justify-between items-center relative'>
            {/* Left: Logo */}
            <NavLink className="flex items-center space-x-2" to='/'>
                <img src={assets.logo} className='h-8 w-8 object-contain cursor-pointer' alt='logo' />
                <span className='ml-2 text-xl sm:text-2xl font-semibold text-indigo-700 cursor-pointer'>
                    removeBg<span className='text-gray-700'>.io</span>
                </span>
            </NavLink>

            {/* Desktop View */}
            <div className='hidden md:flex items-center space-x-4'>
                <SignedOut>
                    <button
                        className='text-gray-700 hover:text-blue-500 font-medium cursor-pointer'
                        onClick={openLogin}
                    >
                        Login
                    </button>
                    <button
                        onClick={openRegister}
                        className='bg-gray-100 hover:bg-gray-200 font-medium text-gray-700 px-4 py-2 rounded-full transition-all cursor-pointer'
                    >
                        Sign up
                    </button>
                </SignedOut>

                <SignedIn>
                    <div className='flex items-center gap-3'>
                        <Link to='/pricing'>
                            <button className='flex items-center gap-2 bg-blue-50 px-4 py-2 rounded-full hover:scale-105 transition duration-300 cursor-pointer'>
                                <img src={assets.credit} alt="credits" className='h-5 w-5' />
                                <span className='text-sm font-medium text-gray-600'>Credits: {credit}</span>
                            </button>
                        </Link>

                        <span className='text-gray-600 hidden sm:block'>Hi, {user?.fullName}</span>
                        <UserButton />
                    </div>
                </SignedIn>
            </div>

            {/* Mobile View Hamburger */}
            <div className='flex md:hidden items-center'>
                <button onClick={() => setMenuOpen(!menuOpen)} className='cursor-pointer'>
                    {menuOpen ? <X size={28} /> : <Menu size={28} />}
                </button>
            </div>

            {/* Mobile Menu */}
            {menuOpen && (
                <div className='absolute top-20 right-6 bg-white shadow-lg rounded-lg flex flex-col space-y-4 p-4 w-52 z-50 md:hidden'>
                    <SignedOut>
                        <button
                            onClick={openLogin}
                            className='text-gray-700 hover:text-blue-500 font-medium'
                        >
                            Login
                        </button>
                        <button
                            onClick={openRegister}
                            className='bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium px-4 py-2 rounded-full'
                        >
                            Sign up
                        </button>
                    </SignedOut>

                    <SignedIn>
                        <div className='flex flex-col gap-3'>
                            <button className='flex items-center gap-2 bg-blue-50 px-4 py-2 rounded-full hover:scale-105 transition duration-300'>
                                <img src={assets.credit} alt="credit" className='h-5 w-5' />
                                <span className='text-sm font-medium text-gray-600'>Credits: {credit}</span>
                            </button>
                            <span className='text-gray-600 text-sm'>Hi, {user?.fullName}</span>
                            <UserButton />
                        </div>
                    </SignedIn>
                </div>
            )}
        </nav>
    );
};

export default Menubar;
