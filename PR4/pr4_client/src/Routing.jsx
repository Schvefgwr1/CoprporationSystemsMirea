import React from "react";
import {BrowserRouter, Routes, Route} from "react-router-dom";
import MainPage from "./components/MainPage.jsx";


export default function Routing() {
    return(
        <BrowserRouter>
            <Routes>
                <Route path="/game" element={<MainPage/>}/>
            </Routes>
        </BrowserRouter>
    )
}