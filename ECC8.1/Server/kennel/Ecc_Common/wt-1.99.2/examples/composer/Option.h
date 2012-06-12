// This may look like C code, but it's really -*- C++ -*-
/*
 * Copyright (C) 2006 Koen Deforche, Kessel-Lo, Belgium.
 *
 * See the LICENSE file for terms of use.
 */
#ifndef OPTION_H_
#define OPTION_H_

#include <WContainerWidget>

namespace Wt {
  class WText;
}

using namespace Wt;

class OptionList;

/**
 * @addtogroup composerexample
 */
/*@{*/

/*! \brief A clickable option
 *
 * This widget is part of the %Wt composer example.
 *
 * On its own, an option is a text which is style "option".
 * An Option may also be used as items in an OptionList.
 *
 * \sa OptionList
 */
class Option : public WContainerWidget
{
public:
  /*! \brief Create an Option with the given text.
   */
  Option(const WMessage& text, WContainerWidget *parent = 0);

  /*! \brief Change the text.
   */
  void setText(const WMessage& text);

  virtual void setHidden(bool);

private:
  //! The option command text.
  WText       *option_;

  //! The separator '|'
  WText       *sep_;

  //! The list in which this option is managed, if managed.
  OptionList  *list_;

  friend class OptionList;

  void setOptionList(OptionList *l);

  //! Create and show the separator.
  void addSeparator();

  //! Show the separator
  void showSeparator();

  //! Hide the separator
  void hideSeparator();
};

/*@}*/

#endif // OPTION_H_
