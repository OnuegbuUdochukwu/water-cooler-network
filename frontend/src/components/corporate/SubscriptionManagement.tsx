import React, { useState, useEffect } from 'react';
import axios from 'axios';
import CorporateNavigation from './CorporateNavigation';
import './SubscriptionManagement.css';

interface Subscription {
  id: number;
  companyId: number;
  planType: string;
  status: string;
  currentPeriodStart: string;
  currentPeriodEnd: string;
  cancelAtPeriodEnd: boolean;
  canceledAt?: string;
  endedAt?: string;
  trialStart?: string;
  trialEnd?: string;
  amount: number;
  currency: string;
  billingCycle: string;
  stripeSubscriptionId?: string;
  stripeCustomerId?: string;
  paymentMethodId?: string;
  metadata?: string;
  createdAt: string;
  updatedAt: string;
}

interface PlanType {
  name: string;
  displayName: string;
  price: number;
  maxEmployees: number;
  hasAdvancedFeatures: boolean;
  hasAnalytics: boolean;
  hasPrioritySupport: boolean;
}

const SubscriptionManagement: React.FC = () => {
  const [subscription, setSubscription] = useState<Subscription | null>(null);
  const [availablePlans, setAvailablePlans] = useState<PlanType[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [showUpgradeModal, setShowUpgradeModal] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState<string>('');
  const [selectedBillingCycle, setSelectedBillingCycle] = useState<'MONTHLY' | 'YEARLY'>('MONTHLY');
  const [companyId, setCompanyId] = useState<number | null>(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      // Get user's company info
      const userResponse = await axios.get('/api/users/profile', { headers });
      const userCompanyId = userResponse.data.companyId;

      if (!userCompanyId) {
        setError('You are not associated with any company');
        return;
      }

      setCompanyId(userCompanyId);

      // Fetch subscription data
      try {
        const subscriptionResponse = await axios.get(`/api/subscriptions/company/${userCompanyId}/active`, { headers });
        setSubscription(subscriptionResponse.data);
      } catch (subError: any) {
        if (subError.response?.status === 404) {
          // No active subscription
          setSubscription(null);
        } else {
          throw subError;
        }
      }

      // Fetch available plans
      const plansResponse = await axios.get('/api/subscriptions/plans', { headers });
      setAvailablePlans(plansResponse.data);

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load subscription data');
    } finally {
      setLoading(false);
    }
  };

  const handleUpgrade = async () => {
    if (!selectedPlan || !companyId) return;

    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      const response = await axios.post('/api/subscriptions', {
        companyId: companyId,
        planType: selectedPlan,
        billingCycle: selectedBillingCycle
      }, { headers });

      setSubscription(response.data);
      setShowUpgradeModal(false);
      setSuccessMessage('Subscription upgraded successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to upgrade subscription');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (cancelAtPeriodEnd: boolean = true) => {
    if (!subscription) return;

    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      const response = await axios.post(`/api/subscriptions/${subscription.id}/cancel?cancelAtPeriodEnd=${cancelAtPeriodEnd}`, {}, { headers });
      
      setSubscription(response.data);
      setSuccessMessage(cancelAtPeriodEnd ? 
        'Subscription will be canceled at the end of the current period' : 
        'Subscription canceled immediately');
      setTimeout(() => setSuccessMessage(''), 3000);

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to cancel subscription');
    } finally {
      setLoading(false);
    }
  };

  const handleReactivate = async () => {
    if (!subscription) return;

    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      const response = await axios.post(`/api/subscriptions/${subscription.id}/reactivate`, {}, { headers });
      
      setSubscription(response.data);
      setSuccessMessage('Subscription reactivated successfully!');
      setTimeout(() => setSuccessMessage(''), 3000);

    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to reactivate subscription');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const formatCurrency = (amount: number, currency: string) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency
    }).format(amount);
  };

  if (loading) {
    return (
      <div className="subscription-management loading">
        <div className="loading-spinner"></div>
        <p>Loading subscription data...</p>
      </div>
    );
  }

  return (
    <div className="subscription-management">
      <CorporateNavigation />
      
      <div className="subscription-header">
        <h1>💳 Subscription Management</h1>
        <p>Manage your company's subscription plan and billing</p>
      </div>

      {error && (
        <div className="error-message">
          <span className="error-icon">⚠️</span>
          <p>{error}</p>
          <button onClick={fetchData} className="retry-btn">Retry</button>
        </div>
      )}

      {successMessage && (
        <div className="success-message">
          <span className="success-icon">✅</span>
          <p>{successMessage}</p>
        </div>
      )}

      <div className="subscription-content">
        {subscription ? (
          <div className="current-subscription">
            <div className="subscription-card">
              <div className="subscription-header">
                <h2>{subscription.planType}</h2>
                <span className={`status-badge ${subscription.status.toLowerCase()}`}>
                  {subscription.status}
                </span>
              </div>

              <div className="subscription-details">
                <div className="detail-row">
                  <span className="label">Billing Cycle:</span>
                  <span className="value">{subscription.billingCycle}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Amount:</span>
                  <span className="value">{formatCurrency(subscription.amount, subscription.currency)}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Current Period:</span>
                  <span className="value">
                    {formatDate(subscription.currentPeriodStart)} - {formatDate(subscription.currentPeriodEnd)}
                  </span>
                </div>
                {subscription.trialEnd && (
                  <div className="detail-row">
                    <span className="label">Trial Ends:</span>
                    <span className="value">{formatDate(subscription.trialEnd)}</span>
                  </div>
                )}
                {subscription.cancelAtPeriodEnd && (
                  <div className="detail-row">
                    <span className="label">Status:</span>
                    <span className="value warning">Will cancel at period end</span>
                  </div>
                )}
              </div>

              <div className="subscription-actions">
                {subscription.status === 'CANCELED' ? (
                  <button onClick={handleReactivate} className="reactivate-btn">
                    Reactivate Subscription
                  </button>
                ) : (
                  <>
                    <button onClick={() => handleCancel(true)} className="cancel-btn">
                      Cancel at Period End
                    </button>
                    <button onClick={() => handleCancel(false)} className="cancel-now-btn">
                      Cancel Immediately
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
        ) : (
          <div className="no-subscription">
            <div className="no-subscription-content">
              <span className="no-subscription-icon">💳</span>
              <h3>No Active Subscription</h3>
              <p>Your company is currently on the free plan. Upgrade to unlock premium features!</p>
              <button onClick={() => setShowUpgradeModal(true)} className="upgrade-btn">
                View Plans & Upgrade
              </button>
            </div>
          </div>
        )}

        <div className="available-plans">
          <h3>Available Plans</h3>
          <div className="plans-grid">
            {availablePlans.map((plan) => (
              <div key={plan.name} className="plan-card">
                <div className="plan-header">
                  <h4>{plan.displayName}</h4>
                  <div className="plan-price">
                    <span className="price-amount">${plan.price}</span>
                    <span className="price-period">/month</span>
                  </div>
                </div>
                
                <div className="plan-features">
                  <div className="feature">
                    <span className="feature-icon">👥</span>
                    <span>Up to {plan.maxEmployees} employees</span>
                  </div>
                  {plan.hasAdvancedFeatures && (
                    <div className="feature">
                      <span className="feature-icon">🚀</span>
                      <span>Advanced features</span>
                    </div>
                  )}
                  {plan.hasAnalytics && (
                    <div className="feature">
                      <span className="feature-icon">📊</span>
                      <span>Analytics dashboard</span>
                    </div>
                  )}
                  {plan.hasPrioritySupport && (
                    <div className="feature">
                      <span className="feature-icon">🎯</span>
                      <span>Priority support</span>
                    </div>
                  )}
                </div>

                {subscription && subscription.planType === plan.name ? (
                  <div className="current-plan-badge">Current Plan</div>
                ) : (
                  <button 
                    onClick={() => {
                      setSelectedPlan(plan.name);
                      setShowUpgradeModal(true);
                    }}
                    className="select-plan-btn"
                    disabled={!!(subscription && subscription.planType === plan.name)}
                  >
                    {subscription && subscription.planType === plan.name ? 'Current Plan' : 'Select Plan'}
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Upgrade Modal */}
      {showUpgradeModal && (
        <div className="modal-overlay">
          <div className="modal">
            <div className="modal-header">
              <h3>Upgrade Subscription</h3>
              <button onClick={() => setShowUpgradeModal(false)} className="close-btn">×</button>
            </div>
            
            <div className="modal-content">
              <div className="plan-selection">
                <label>Selected Plan:</label>
                <select value={selectedPlan} onChange={(e) => setSelectedPlan(e.target.value)}>
                  <option value="">Select a plan</option>
                  {availablePlans.map((plan) => (
                    <option key={plan.name} value={plan.name}>
                      {plan.displayName} - ${plan.price}/month
                    </option>
                  ))}
                </select>
              </div>

              <div className="billing-selection">
                <label>Billing Cycle:</label>
                <div className="billing-options">
                  <label>
                    <input
                      type="radio"
                      value="MONTHLY"
                      checked={selectedBillingCycle === 'MONTHLY'}
                      onChange={(e) => setSelectedBillingCycle(e.target.value as 'MONTHLY' | 'YEARLY')}
                    />
                    Monthly
                  </label>
                  <label>
                    <input
                      type="radio"
                      value="YEARLY"
                      checked={selectedBillingCycle === 'YEARLY'}
                      onChange={(e) => setSelectedBillingCycle(e.target.value as 'MONTHLY' | 'YEARLY')}
                    />
                    Yearly (20% discount)
                  </label>
                </div>
              </div>

              {selectedPlan && (
                <div className="plan-summary">
                  <h4>Plan Summary</h4>
                  <p>You'll start with a 14-day free trial, then be charged according to your selected plan.</p>
                </div>
              )}
            </div>

            <div className="modal-actions">
              <button onClick={() => setShowUpgradeModal(false)} className="cancel-btn">
                Cancel
              </button>
              <button 
                onClick={handleUpgrade} 
                className="upgrade-btn"
                disabled={!selectedPlan || loading}
              >
                {loading ? 'Processing...' : 'Start Trial'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SubscriptionManagement;
