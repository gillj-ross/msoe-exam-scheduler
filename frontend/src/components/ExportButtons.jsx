import React, { useState } from 'react';
import { getGoogleCalendarUrl, exportIcs, getWebcalUrl } from '../api/examApi';

/**
 * Shows export action buttons when exams are selected.
 * Props:
 *   selectedIds - array of selected exam IDs
 */
function ExportButtons({ selectedIds }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  if (!selectedIds || selectedIds.length === 0) return null;

  const handleGoogleCalendar = async () => {
    setError(null);
    try {
      for (const id of selectedIds) {
        const res = await getGoogleCalendarUrl(id);
        window.open(res.data.url, '_blank', 'noopener,noreferrer');
      }
    } catch {
      setError('Failed to open Google Calendar. Please try again.');
    }
  };

  const handleDownloadIcs = async () => {
    setError(null);
    setLoading(true);
    try {
      const res = await exportIcs(selectedIds);
      const url = URL.createObjectURL(new Blob([res.data], { type: 'text/calendar' }));
      const link = document.createElement('a');
      link.href = url;
      link.download = 'msoe-exams.ics';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    } catch {
      setError('Failed to download ICS file. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleAppleCalendar = async () => {
    setError(null);
    try {
      const res = await getWebcalUrl();
      window.location.href = res.data.url;
    } catch {
      setError('Failed to open Apple Calendar link. Please try again.');
    }
  };

  return (
    <div className="export-buttons">
      <span className="export-label">
        {selectedIds.length} exam{selectedIds.length !== 1 ? 's' : ''} selected — Export to:
      </span>

      <button
        className="btn btn-google"
        onClick={handleGoogleCalendar}
        title="Opens each selected exam in Google Calendar"
      >
        📅 Google Calendar
      </button>

      <button
        className="btn btn-ics"
        onClick={handleDownloadIcs}
        disabled={loading}
        title="Download a .ics file (works with Outlook and most calendar apps)"
      >
        {loading ? 'Downloading…' : '⬇️ Download ICS (Outlook)'}
      </button>

      <button
        className="btn btn-apple"
        onClick={handleAppleCalendar}
        title="Subscribe in Apple Calendar (best on iPhone/iPad/Mac)"
      >
        🍎 Apple Calendar
      </button>

      {error && <p className="export-error">{error}</p>}
    </div>
  );
}

export default ExportButtons;
