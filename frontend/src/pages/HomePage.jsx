import React, { useState } from 'react';
import ExamList from '../components/ExamList';
import CalendarView from '../components/CalendarView';

function HomePage() {
  const [view, setView] = useState('list'); // 'list' | 'calendar'

  return (
    <div className="page home-page">
      <header className="page-header">
        <h1>MSOE Final Exam Scheduler</h1>
        <p className="tagline">
          Find your finals, add them to your calendar — no login required.
        </p>
      </header>

      <div className="view-toggle">
        <button
          className={`toggle-btn ${view === 'list' ? 'active' : ''}`}
          onClick={() => setView('list')}
        >
          List View
        </button>
        <button
          className={`toggle-btn ${view === 'calendar' ? 'active' : ''}`}
          onClick={() => setView('calendar')}
        >
          Calendar View
        </button>
      </div>

      {view === 'list' ? <ExamList /> : <CalendarView />}
    </div>
  );
}

export default HomePage;
