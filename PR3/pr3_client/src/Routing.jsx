import React from "react";
import {BrowserRouter, Routes, Route} from "react-router-dom";
import MainPage from "./MainPage.jsx";


export default function Routing() {
    return(
        <BrowserRouter>
            <Routes>
                <Route path="/file" element={<MainPage/>}/>
            </Routes>
        </BrowserRouter>
    )
}