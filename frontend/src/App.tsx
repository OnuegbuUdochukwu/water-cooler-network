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
import AchievementsDashboard from "./components/gamification/AchievementsDashboard";
import CompanyDashboard from "./components/corporate/CompanyDashboard";
import DepartmentManagement from "./components/corporate/DepartmentManagement";
import InvitationManagement from "./components/corporate/InvitationManagement";
import CompanySettings from "./components/corporate/CompanySettings";
import CompanyAnnouncements from "./components/corporate/CompanyAnnouncements";
import EmployeeDirectory from "./components/corporate/EmployeeDirectory";
import SubscriptionManagement from "./components/corporate/SubscriptionManagement";
import SmartMatching from './components/SmartMatching';
import MeetingScheduler from './components/MeetingScheduler';
import MatchFeedback from './components/MatchFeedback';
import AnalyticsDashboard from './components/analytics/AnalyticsDashboard';
import MentorshipDashboard from './components/mentorship/MentorshipDashboard';
import UserInsights from './components/UserInsights';
import AdvancedSearch from './components/AdvancedSearch';
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
                                path="/achievements"
                                element={
                                    <ProtectedRoute>
                                        <AchievementsDashboard />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/corporate"
                                element={
                                    <ProtectedRoute>
                                        <CompanyDashboard />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/corporate/departments"
                                element={
                                    <ProtectedRoute>
                                        <DepartmentManagement />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/corporate/invitations"
                                element={
                                    <ProtectedRoute>
                                        <InvitationManagement />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/corporate/announcements"
                                element={
                                    <ProtectedRoute>
                                        <CompanyAnnouncements />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/corporate/settings"
                                element={
                                    <ProtectedRoute>
                                        <CompanySettings />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/corporate/subscriptions"
                                element={
                                    <ProtectedRoute>
                                        <SubscriptionManagement />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/corporate/employees"
                                element={
                                    <ProtectedRoute>
                                        <EmployeeDirectory />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/smart-matching"
                                element={
                                    <ProtectedRoute>
                                        <SmartMatching />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/meetings"
                                element={
                                    <ProtectedRoute>
                                        <MeetingScheduler />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/feedback"
                                element={
                                    <ProtectedRoute>
                                        <MatchFeedback />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/analytics"
                                element={
                                    <ProtectedRoute>
                                        <AnalyticsDashboard />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/mentorship"
                                element={
                                    <ProtectedRoute>
                                        <MentorshipDashboard />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/insights"
                                element={
                                    <ProtectedRoute>
                                        <UserInsights />
                                    </ProtectedRoute>
                                }
                            />
                            <Route
                                path="/search"
                                element={
                                    <ProtectedRoute>
                                        <AdvancedSearch />
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
