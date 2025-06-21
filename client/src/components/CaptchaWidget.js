import React, { useState, useEffect } from 'react';
import { Card, Button, Form, Alert, Spinner } from 'react-bootstrap';
import { RefreshCw, Shield, CheckCircle, XCircle } from 'lucide-react';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid';

const CaptchaWidget = ({ onValidation, required = true, className = '' }) => {
  const [captchaData, setCaptchaData] = useState(null);
  const [userInput, setUserInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [validating, setValidating] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [sessionId] = useState(() => uuidv4());
  const [attempts, setAttempts] = useState(0);
  const [blocked, setBlocked] = useState(false);
  const [blockUntil, setBlockUntil] = useState(null);

  useEffect(() => {
    generateCaptcha();
  }, []);

  useEffect(() => {
    if (blocked && blockUntil) {
      const timer = setInterval(() => {
        if (new Date() > new Date(blockUntil)) {
          setBlocked(false);
          setBlockUntil(null);
          setAttempts(0);
          generateCaptcha();
        }
      }, 1000);

      return () => clearInterval(timer);
    }
  }, [blocked, blockUntil]);

  const generateCaptcha = async () => {
    if (blocked) return;

    setLoading(true);
    setError('');
    setSuccess(false);
    setUserInput('');

    try {
      const response = await axios.get('/api/captcha/generate', {
        headers: {
          'x-session-id': sessionId
        }
      });

      setCaptchaData(response.data);
    } catch (error) {
      if (error.response?.status === 429) {
        setBlocked(true);
        setBlockUntil(error.response.data.blockUntil);
        setError('Too many attempts. Please wait before requesting a new CAPTCHA.');
      } else {
        setError('Failed to generate CAPTCHA. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const validateCaptcha = async () => {
    if (!userInput.trim()) {
      setError('Please enter the CAPTCHA code');
      return;
    }

    setValidating(true);
    setError('');

    try {
      const response = await axios.post('/api/captcha/validate', {
        captchaId: captchaData.captchaId,
        code: userInput.trim(),
        sessionId: sessionId
      });

      if (response.data.valid) {
        setSuccess(true);
        setError('');
        if (onValidation) {
          onValidation({
            valid: true,
            captchaId: captchaData.captchaId,
            code: userInput.trim(),
            sessionId: sessionId
          });
        }
      }
    } catch (error) {
      const errorData = error.response?.data;
      
      if (error.response?.status === 429) {
        setBlocked(true);
        setBlockUntil(errorData.blockUntil);
        setError('Too many failed attempts. Please wait before trying again.');
      } else {
        setError(errorData?.message || 'Invalid CAPTCHA code');
        setAttempts(prev => prev + 1);
        
        if (errorData?.attemptsRemaining !== undefined) {
          setError(`Invalid CAPTCHA code. ${errorData.attemptsRemaining} attempts remaining.`);
        }
      }
      
      if (onValidation) {
        onValidation({ valid: false });
      }
    } finally {
      setValidating(false);
    }
  };

  const handleRefresh = () => {
    setSuccess(false);
    generateCaptcha();
  };

  const getTimeRemaining = () => {
    if (!blockUntil) return '';
    
    const now = new Date();
    const until = new Date(blockUntil);
    const diff = Math.max(0, Math.ceil((until - now) / 1000));
    
    const minutes = Math.floor(diff / 60);
    const seconds = diff % 60;
    
    return `${minutes}:${seconds.toString().padStart(2, '0')}`;
  };

  return (
    <Card className={`captcha-widget ${className}`}>
      <Card.Header className="d-flex align-items-center justify-content-between">
        <div className="d-flex align-items-center">
          <Shield size={20} className="me-2" />
          <span className="fw-semibold">CAPTCHA Verification</span>
        </div>
        {required && <small className="text-light">Required</small>}
        }
      </Card.Header>
      
      <Card.Body>
        {blocked ? (
          <Alert variant="warning" className="text-center">
            <XCircle size={24} className="mb-2" />
            <div>Too many attempts. Please wait {getTimeRemaining()} before trying again.</div>
          </Alert>
        ) : (
          <>
            <div className="text-center mb-3">
              {loading ? (
                <div className="d-flex align-items-center justify-content-center" style={{ height: '120px' }}>
                  <Spinner animation="border" variant="primary" />
                </div>
              ) : captchaData ? (
                <div className="position-relative">
                  <img 
                    src={captchaData.image} 
                    alt="CAPTCHA" 
                    className="img-fluid border rounded"
                    style={{ maxHeight: '120px', backgroundColor: '#f8f9fa' }}
                  />
                  <Button
                    variant="outline-secondary"
                    size="sm"
                    className="position-absolute top-0 end-0 m-2"
                    onClick={handleRefresh}
                    disabled={loading || validating}
                    title="Refresh CAPTCHA"
                  >
                    <RefreshCw size={16} />
                  </Button>
                </div>
              ) : (
                <div className="text-muted">Failed to load CAPTCHA</div>
              )}
            </div>

            <Form.Group className="mb-3">
              <Form.Label className="fw-semibold">
                Enter the code shown above:
              </Form.Label>
              <Form.Control
                type="text"
                value={userInput}
                onChange={(e) => setUserInput(e.target.value.toUpperCase())}
                placeholder="Enter CAPTCHA code"
                disabled={loading || validating || success}
                maxLength={6}
                className={success ? 'is-valid' : error ? 'is-invalid' : ''}
                onKeyPress={(e) => {
                  if (e.key === 'Enter') {
                    e.preventDefault();
                    validateCaptcha();
                  }
                }}
              />
            </Form.Group>

            {error && (
              <Alert variant="danger" className="d-flex align-items-center">
                <XCircle size={16} className="me-2" />
                {error}
              </Alert>
            )}

            {success && (
              <Alert variant="success" className="d-flex align-items-center">
                <CheckCircle size={16} className="me-2" />
                CAPTCHA verified successfully!
              </Alert>
            )}

            <div className="d-flex gap-2">
              <Button
                variant="primary"
                onClick={validateCaptcha}
                disabled={loading || validating || success || !userInput.trim()}
                className="flex-grow-1"
              >
                {validating ? (
                  <>
                    <Spinner size="sm" className="me-2" />
                    Verifying...
                  </>
                ) : (
                  'Verify'
                )}
              </Button>
              
              <Button
                variant="outline-secondary"
                onClick={handleRefresh}
                disabled={loading || validating}
                title="Get new CAPTCHA"
              >
                <RefreshCw size={16} />
              </Button>
            </div>

            {attempts > 0 && !success && (
              <small className="text-muted mt-2 d-block">
                Attempts: {attempts}/5
              </small>
            )}
          </>
        )}
      </Card.Body>
    </Card>
  );
};

export default CaptchaWidget;