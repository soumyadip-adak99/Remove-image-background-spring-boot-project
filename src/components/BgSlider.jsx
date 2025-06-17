import { useState } from 'react'
import { assets, category } from '../assets/assets'

const BgSlider = () => {
    const [position, setPosition] = useState(50)
    const [activeCategory, setActiveCategory] = useState("People")

    const handleSliderChange = (e) => {
        setPosition(e.target.value)
    }

    return (
        <div className='mb-16 relative'>
            {/* section title */}
            <h2 className='text-3xl md:text-4xl font-bold text-gray-900 mb-12 text-center'>Stunning quality</h2>

            {/* catagory selector */}
            <div className='flex justify-center mb-10 flex-wrap'>
                <div className='inline-flex gap-4 bg-gray-100 p-2 rounded-full flex-wrap justify-center shadow-lg'>
                    {category.map((catagory) => (
                        <button key={catagory}
                            onClick={() => setActiveCategory(catagory)}
                            className={`px-6 py-2 rounded-full font-medium ${activeCategory === catagory ? 'bg-white text-gray-800 shadow-sm ' : 'text-gray-600 hover:bg-gray-200'} cursor-pointer `}>
                            {catagory}
                        </button>
                    ))}
                </div>
            </div>


            {/* {image comparsion slider} */}
            <div className='relative w-full max-w-4xl overflow-hidden m-auto rounded-xl shadow-lg aspect-[3/2]'>
                <img
                    src={assets.people_org}
                    alt="original image"
                    style={{ clipPath: `inset(0 ${100 - position}% 0 0)` }}
                    className="w-full h-full object-cover"
                />
                <img
                    src={assets.people}
                    alt="removed background image"
                    style={{ clipPath: `inset(0 0 0 ${position}%)` }}
                    className='absolute top-0 left-0 w-full h-full object-cover bg-white'
                />

                <div
                    className="absolute top-0 h-full w-[2px] bg-white z-20"
                    style={{ left: `${position}%`, transform: 'translateX(-50%)' }}
                ></div>

                <input
                    type="range"
                    min={0}
                    max={100}
                    value={position}
                    onChange={handleSliderChange}
                    className="slider absolute top-0 left-0 w-full h-full z-10 bg-white"
                />
            </div>


        </div>
    )
}

export default BgSlider 