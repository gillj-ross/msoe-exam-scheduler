import React, { useState, useEffect } from 'react';
import { getExams } from '../api/examApi';
import ExamCard from './ExamCard';
import ExportButtons from './ExportButtons';

/**
 * Searchable, filterable list of exams with checkboxes for selection.
 * Props:
 *   onEdit - callback(exam) called when admin clicks Edit (only shown on /admin)
 *   showEdit - boolean, whether to show edit/delete controls
 *   onDelete - callback(id) for admin delete
 */
function ExamList({ onEdit, showEdit = false, onDelete }) {
  const [exams, setExams] = useState([]);
  const [search, setSearch] = useState('');
  const [semester, setSemester] = useState('');
  const [selectedIds, setSelectedIds] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const semesters = [
    'Spring 2026',
    'Fall 2025',
    'Spring 2025',
    'Fall 2024',
  ];

  useEffect(() => {
    fetchExams();
  }, [search, semester]);

  const fetchExams = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await getExams(search || undefined, semester || undefined);
      setExams(res.data);
    } catch {
      setError('Failed to load exams. Make sure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const toggleSelect = (id) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]
    );
  };

  const toggleSelectAll = () => {
    if (selectedIds.length === exams.length) {
      setSelectedIds([]);
    } else {
      setSelectedIds(exams.map((e) => e.id));
    }
  };

  const handleSearchInput = (e) => {
    setSearch(e.target.value);
    setSelectedIds([]);
  };

  const handleSemesterChange = (e) => {
    setSemester(e.target.value);
    setSelectedIds([]);
  };

  return (
    <div className="exam-list">
      <div className="filters">
        <input
          type="text"
          className="search-input"
          placeholder="Search by course code (e.g. CS2852)"
          value={search}
          onChange={handleSearchInput}
          aria-label="Search exams"
        />
        <select
          className="semester-select"
          value={semester}
          onChange={handleSemesterChange}
          aria-label="Filter by semester"
        >
          <option value="">All Semesters</option>
          {semesters.map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
      </div>

      <ExportButtons selectedIds={selectedIds} />

      {loading && <p className="status-msg">Loading exams…</p>}
      {error && <p className="status-msg error">{error}</p>}

      {!loading && !error && exams.length === 0 && (
        <p className="status-msg">No exams found. Try a different search term.</p>
      )}

      {!loading && exams.length > 0 && (
        <div className="table-wrapper">
          <table className="exams-table">
            <thead>
              <tr>
                <th>
                  <input
                    type="checkbox"
                    checked={selectedIds.length === exams.length && exams.length > 0}
                    onChange={toggleSelectAll}
                    aria-label="Select all exams"
                  />
                </th>
                <th>Course</th>
                <th>Section</th>
                <th>Name</th>
                <th>Instructor</th>
                <th>Date</th>
                <th>Time</th>
                <th>Room</th>
                <th>Semester</th>
                {showEdit && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {exams.map((exam) => (
                <React.Fragment key={exam.id}>
                  <ExamCard
                    exam={exam}
                    selected={selectedIds.includes(exam.id)}
                    onToggle={toggleSelect}
                  />
                  {showEdit && (
                    <tr className="admin-actions-row">
                      <td colSpan={9} style={{ textAlign: 'right', paddingBottom: '4px' }}>
                        <button
                          className="btn btn-sm btn-edit"
                          onClick={() => onEdit(exam)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-sm btn-delete"
                          onClick={() => onDelete(exam.id)}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  )}
                </React.Fragment>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default ExamList;
