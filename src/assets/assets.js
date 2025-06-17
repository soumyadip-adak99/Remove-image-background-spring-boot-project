import logo from '../assets/eraser_8220266.png'
import hero_video from '../assets/home-page-banner.mp4'
import people from '../assets/people.png'
import people_org from '../assets/people-org.png'
import credit from '../assets/dollar.png'

export const assets = {
    logo,
    hero_video,
    people,
    people_org,
    credit
}

export const steps = [
    {
        step: "Step 1",
        title: "Select an image",
        description: `First, choose the image you want to remove background from by clicking on Start from a photo. 
            Your image format can be PNG or JPG. We support all image dimensions.`
    },
    {
        step: "Step 2",
        title: "Let magic remove the background",
        description: `Our tool automatically removes the background from your image. Next, you can choose a background color. 
            Our most popular options are white and transparent backgrounds, but you can pick any color you like.`
    },
    {
        step: "Step 3",
        title: "Download your image",
        description: `After selecting a new background color, download your photo and you're done! 
                You can also save your picture in the Photoroom App by creating an account.`
    }
]

export const category = ['People', 'Products', 'Animal', 'Cars', 'Graphics']


export const plans = [
    {
        id: "Basic",
        name: "Basic Plan",
        price: 499,
        credits: 100,
        description: "Perfect for occasional use",
        popular: false
    },
    {
        id: "Premium",
        name: "Premium Plan",
        price: 899,
        credits: 250,
        description: "Great for regular users",
        popular: true
    },
    {
        id: "Ultimate",
        name: "Ultimate Plan",
        price: 1499,
        credits: 1000,
        description: "For power users",
        popular: false
    }
];

export const testimonials = [
    {
        id: 1,
        quote: "We are impressed by the AI and think it's the best choice on the market.",
        author: "Anthony Walker",
        handle: "@_webarchitect_"
    },
    {
        id: 2,
        quote: "remove.bg is leaps and bounds ahead of the competition. A thousand times better. It simplified the whole process.",
        author: "Sarah Johnson",
        handle: "@techlead_sarah"
    },
    {
        id: 3,
        quote: "We were impressed by its ability to account for pesky, feathery hair without making an image look jagged and amateurish.",
        author: "Michael Chen",
        handle: "@coding_newbie"
    }
]

export const FOOTER_CONSTANTS = [
    {
        url: "https://facebook.com",
        logo: "https://img.icons8.com/fluent/30/000000/facebook-new.png"
    },
    {
        url: "https://linkedin.com",
        logo: "https://img.icons8.com/fluent/30/000000/linkedin-2.png"
    },
    {
        url: "https://instagram.com",
        logo: "https://img.icons8.com/fluent/30/000000/instagram-new.png"
    },
    {
        url: "https://twitter.com",
        logo: "https://img.icons8.com/fluent/30/000000/twitter.png"
    }
]

