import { useContext } from "react";
import { AppContext } from "../context/AppContext";
import { useNavigate } from "react-router-dom";

const Result = () => {
    const { image, resultImage, setImage, setResultImage } = useContext(AppContext);
    const navigate = useNavigate();

    const handleTryAnother = () => {
        setImage(null);
        setResultImage(null);
        navigate("/"); // Redirect to upload page
    };

    return (
        <div className="px-4 lg:px-44 py-8 min-h-[75vh]">
            <div className="bg-white rounded-xl shadow-lg p-6 flex flex-col md:flex-row gap-6">
                {/* Original Image */}
                <div className="w-full md:w-1/2 flex flex-col items-center">
                    <p className="text-gray-600 font-semibold mb-2">Original</p>
                    {image ? (
                        <img
                            src={URL.createObjectURL(image)}
                            alt="original"
                            className="rounded-lg w-full max-h-[400px] object-contain border"
                        />
                    ) : (
                        <p className="text-gray-400">No original image</p>
                    )}
                </div>

                {/* Result Image */}
                <div className="w-full md:w-1/2 flex flex-col items-center">
                    <p className="text-gray-600 font-semibold mb-2">Background Removed</p>
                    <div className="w-full h-full min-h-[200px] border border-gray-300 bg-gray-50 rounded-lg flex items-center justify-center relative overflow-hidden">
                        {resultImage ? (
                            <img
                                src={resultImage}
                                alt="result"
                                className="w-full max-h-[400px] object-contain"
                            />
                        ) : image ? (
                            <div className="h-12 w-12 border-4 border-indigo-600 border-t-transparent rounded-full animate-spin" />
                        ) : (
                            <p className="text-gray-400">No image processed</p>
                        )}
                    </div>
                </div>
            </div>

            {/* Buttons */}
            {resultImage && (
                <div className="flex flex-col sm:flex-row justify-center md:justify-end items-center gap-4 mt-6">
                    <button
                        onClick={handleTryAnother}
                        className="border border-indigo-600 text-indigo-600 font-semibold py-2 px-6 rounded-full hover:bg-indigo-50 transition-all duration-300 text-lg"
                    >
                        Try another image
                    </button>
                    <a
                        href={resultImage}
                        download="background-removed.png"
                        className="bg-gradient-to-r from-purple-500 to-indigo-500 text-white font-semibold py-3 px-6 rounded-full shadow-md hover:from-purple-600 hover:to-indigo-600 transform hover:scale-105 transition duration-300"
                    >
                        Download image
                    </a>
                </div>
            )}
        </div>
    );
};

export default Result;
