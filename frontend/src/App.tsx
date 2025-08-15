import React from "react";
import {
    BrowserRouter as Router,
    Routes,
    Route,
    Navigate,
} from "react-router-dom";
import "./App.css";
import Navbar from "./components/Navbar";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import Profile from "./components/profile/Profile";
import Dashboard from "./components/dashboard/Dashboard";
import MatchFinder from "./components/matching/MatchFinder";
import MyMatches from "./components/matching/MyMatches";
import LoungeDiscovery from "./components/lounges/LoungeDiscovery";
import CreateLounge from "./components/lounges/CreateLounge";
import LoungeChat from "./components/lounges/LoungeChat";
import { AuthProvider, useAuth } from "./contexts/AuthContext";

function App() {
    return (
        <AuthProvider>
            <Router>
                <div className="App">
                    <Navbar />
                    <main className="container">
                        <Routes>
                            <Route path="/login" element={<Login />} />
                            <Route path="/register" element={<Register />} />
                            <Route
                                path="/profile"
                                element={
                                    <ProtectedRoute>
                                        <Profile />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/dashboard"
                                element={
                                    <ProtectedRoute>
                                        <Dashboard />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/matches"
                                element={
                                    <ProtectedRoute>
                                        <MatchFinder />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/my-matches"
                                element={
                                    <ProtectedRoute>
                                        <MyMatches />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/lounges"
                                element={
                                    <ProtectedRoute>
                                        <LoungeDiscovery />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/lounges/create"
                                element={
                                    <ProtectedRoute>
                                        <CreateLounge />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/lounges/:loungeId"
                                element={
                                    <ProtectedRoute>
                                        <LoungeChat />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/"
                                element={<Navigate to="/dashboard" replace />}
                            />
                        </Routes>
                    </main>
                </div>
            </Router>
        </AuthProvider>
    );
}

function ProtectedRoute({ children }: { children: React.ReactNode }) {
    const { isAuthenticated } = useAuth();
    return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />;
}

export default App;
