import React, { useState, useEffect } from 'react';

const EMPTY_FORM = {
  courseCode: '',
  courseSection: '',
  courseName: '',
  instructor: '',
  examDate: '',
  startTime: '',
  endTime: '',
  location: '',
  semester: 'Spring 2026',
  notes: '',
};

/**
 * Modal form for creating or editing an exam.
 * Props:
 *   exam     - existing exam object (null for create mode)
 *   onSave   - callback(formData) when form is submitted
 *   onCancel - callback() when modal is dismissed
 */
function ExamForm({ exam, onSave, onCancel }) {
  const [form, setForm] = useState(EMPTY_FORM);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (exam) {
      setForm({
        courseCode: exam.courseCode || '',
        courseSection: exam.courseSection || '',
        courseName: exam.courseName || '',
        instructor: exam.instructor || '',
        examDate: exam.examDate || '',
        startTime: exam.startTime || '',
        endTime: exam.endTime || '',
        location: exam.location || '',
        semester: exam.semester || 'Spring 2026',
        notes: exam.notes || '',
      });
    } else {
      setForm(EMPTY_FORM);
    }
    setErrors({});
  }, [exam]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: null }));
  };

  const validate = () => {
    const newErrors = {};
    if (!form.courseCode.trim()) newErrors.courseCode = 'Course code is required.';
    if (!form.examDate) newErrors.examDate = 'Exam date is required.';
    return newErrors;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const validation = validate();
    if (Object.keys(validation).length > 0) {
      setErrors(validation);
      return;
    }
    onSave(form);
  };

  const semesters = ['Spring 2026', 'Fall 2025', 'Spring 2025', 'Fall 2024'];

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h2>{exam ? 'Edit Exam' : 'Add New Exam'}</h2>

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-row">
            <div className="form-group">
              <label>Course Code *</label>
              <input
                name="courseCode"
                value={form.courseCode}
                onChange={handleChange}
                placeholder="CS2852"
              />
              {errors.courseCode && <span className="field-error">{errors.courseCode}</span>}
            </div>

            <div className="form-group">
              <label>Section</label>
              <input
                name="courseSection"
                value={form.courseSection}
                onChange={handleChange}
                placeholder="001"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Course Name</label>
            <input
              name="courseName"
              value={form.courseName}
              onChange={handleChange}
              placeholder="Data Structures"
            />
          </div>

          <div className="form-group">
            <label>Instructor</label>
            <input
              name="instructor"
              value={form.instructor}
              onChange={handleChange}
              placeholder="Prof. Jones"
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Exam Date *</label>
              <input
                type="date"
                name="examDate"
                value={form.examDate}
                onChange={handleChange}
              />
              {errors.examDate && <span className="field-error">{errors.examDate}</span>}
            </div>

            <div className="form-group">
              <label>Semester</label>
              <select name="semester" value={form.semester} onChange={handleChange}>
                {semesters.map((s) => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Start Time</label>
              <input
                type="time"
                name="startTime"
                value={form.startTime}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label>End Time</label>
              <input
                type="time"
                name="endTime"
                value={form.endTime}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="form-group">
            <label>Location / Room</label>
            <input
              name="location"
              value={form.location}
              onChange={handleChange}
              placeholder="S202"
            />
          </div>

          <div className="form-group">
            <label>Notes</label>
            <textarea
              name="notes"
              value={form.notes}
              onChange={handleChange}
              placeholder="e.g. Closed-book, bring calculator"
              rows={2}
            />
          </div>

          <div className="form-actions">
            <button type="button" className="btn btn-secondary" onClick={onCancel}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary">
              {exam ? 'Save Changes' : 'Add Exam'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ExamForm;
